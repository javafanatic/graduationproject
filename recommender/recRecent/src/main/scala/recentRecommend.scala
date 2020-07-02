import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis
import scala.collection.JavaConversions._

case class MongConfig(uri:String,db:String)

//推荐po
case class Recommendation(rid:Int, r:Double)

// 用户的推荐
case class UserRecs(uid:Int, recs:Seq[Recommendation])
//电影相似度矩阵
case class MovieRecs(mid:Int, recs:Seq[Recommendation])
object MyHelperToConnect extends Serializable{
  lazy val jedis = new Jedis("182.61.43.77")
  lazy val mongoClient = MongoClient(MongoClientURI("mongodb://master01:27017/recommender"))
}
object recentRecommender {

  //入口方法
  def main(args: Array[String]): Unit = {

    //创建一个SparkConf配置
    val sparkConf = new SparkConf().setAppName("RecentRecommend").setMaster("local[*]")
    //创建Spark的对象
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    val stream = new StreamingContext(sc, Seconds(2))

    implicit val mongConfig = MongConfig("mongodb://master01:27017/recommender","recommender")
    import spark.implicits._

    //******************  广播电影相似度矩阵

    //装换成为 Map[Int, Map[Int,Double]]
    val tmp_data= spark
      .read
      .option("uri", "mongodb://master01:27017/recommender")
      .option("collection", "MovieRecs")
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRecs]
      .rdd
      .map { item =>
        (item.mid, item.recs.map(x => (x.rid, x.r)).toMap)
      }.collectAsMap()

    val simMoviesMatrixBroadCast = sc.broadcast(tmp_data)

    val xxx = sc.makeRDD(1 to 2)
    xxx.map(x => simMoviesMatrixBroadCast.value.get(1)).count()


    //kafka参数
    val kafkaPara = Map(
      "bootstrap.servers" -> "master01:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "recommender",
      "auto.offset.reset" -> "latest"
    )
    val kafkaStream = KafkaUtils.createDirectStream[String, String](stream, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String, String](Array("recommender"), kafkaPara))
    //kafka消费者接收到消息
    val ratingStream = kafkaStream.map { item =>
      var attr = item.value().split("\\|")
      (attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }
    ratingStream.foreachRDD { rdd =>
      rdd.map { case (uid, mid, score, timestamp) =>
        println("识别到评分流")

        //获取当前最近的M次电影评分
        val rset = getRatingByuid(10, uid, MyHelperToConnect.jedis)

        //获取电影P最相似的K个电影
        val kset = getTopSimMovies(20, mid, uid, simMoviesMatrixBroadCast.value)

        //计算待选电影的推荐优先级
        val streamRecs = compute(simMoviesMatrixBroadCast.value, rset, kset)

        //将数据保存到MongoDB
        save(uid, streamRecs)

      }.count()
    }

    //启动Streaming程序
    stream.start()
    stream.awaitTermination()
  }



  def compute(simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]], rset: Array[(Int, Double)], kset: Array[Int]): Array[(Int, Double)] = {

    val score = scala.collection.mutable.ArrayBuffer[(Int, Double)]()

    val increase = scala.collection.mutable.HashMap[Int, Int]()

    val decrease = scala.collection.mutable.HashMap[Int, Int]()

    for (topSimMovie <- kset; userRecentlyRating <- rset) {
      val simScore = getMoviesSimScore(simMovies, userRecentlyRating._1, topSimMovie)
      if (simScore > 0.6) {
        score += ((topSimMovie, simScore * userRecentlyRating._2))
        if (userRecentlyRating._2 > 3) {
          increase(topSimMovie) =increase.getOrDefault(topSimMovie, 0) + 1
        } else {
          decrease(topSimMovie) = decrease.getOrDefault(topSimMovie, 0) + 1
        }
      }
    }

    score.groupBy(_._1).map { case (mid, sims) =>
      print(sims)
      if (increase.contains(mid) && decrease.contains(mid)) {
        (mid, sims.map(_._2).sum / sims.length + log(increase(mid)) - log(decrease(mid)))
      }
      else {
        (mid, sims.map(_._2).sum / sims.length) //+ log(increMap(mid)) - log(decreMap(mid)))
      }
    }.toArray

  }

  //取2的对数
  def log(m: Int): Double = {
    math.log(m) / math.log(2)
  }
  def getRatingByuid(num: Int, uid: Int, jedis: Jedis): Array[(Int, Double)] = {
    //从用户的队列中取出num个评论
    jedis.lrange("uid:" + uid.toString, 0, num).map { item =>
      val attr = item.split("\\:")
      (attr(0).trim.toInt, attr(1).trim.toDouble)
    }.toArray
  }

  def getMoviesSimScore(simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]], userRatingMovie: Int, topSimMovie1: Int): Double = {
    simMovies.get(topSimMovie1) match {
      case Some(sim) => sim.get(userRatingMovie) match {
        case Some(score) => score
        case None => 0.0
      }
      case None => 0.0
    }
  }


  def getTopSimMovies(num: Int, mid: Int, uid: Int, movieMap: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]])(implicit mongConfig: MongConfig): Array[Int] = {
    //从广播变量的电影相似度矩阵中获取当前电影所有的相似电影
    val size = movieMap.get(mid).size
    //处理异常
    if (size == 0) {
      return new Array[Int](0)
    }
    else {
      val allSimMovies = movieMap.get(mid).get.toArray
      //过滤掉已经评分过的电影
      val old = MyHelperToConnect.mongoClient("recommender")("Rating").find(MongoDBObject("uid" -> uid)).toArray.map { item =>
        item.get("mid").toString.toInt
      }
      allSimMovies.filter(x => !old.contains(x._1)).sortWith(_._2 > _._2).take(num).map(item => item._1)
    }
  }



  //存储到mongodb中
  def save(uid: Int, streamRecs: Array[(Int, Double)])(implicit mongConfig: MongConfig): Unit = {
    //到StreamRecs的连接
    val collection = MyHelperToConnect.mongoClient(mongConfig.db)("StreamRecs")

    collection.findAndRemove(MongoDBObject("uid" -> uid))
    collection.insert(MongoDBObject("uid" -> uid, "recs" -> streamRecs.map(x => x._1 + ":" + x._2).mkString("|")))

  }


}
