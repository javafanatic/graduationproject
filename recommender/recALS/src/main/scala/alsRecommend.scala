import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix


case class Movie(val mid: Int, val name: String, val video: String, val spic : String, val mpic :String , val lpic:String, val year: String,
                 val actors: String,   val country: String,val summary:String ,val language: String,   val directors: String,val writers: String)

case class MyRating(val uid: Int, val mid: Int, val score: Double, val timestamp: Int)


case class MongoConfig(val uri:String, val db:String)

//推荐中间过度类
case class Recommendation(rid:Int, r:Double)
// 用户的推荐
case class UserRecs(uid:Int, recs:Seq[Recommendation])

//电影的相似度
case class MovieRecs(mid:Int, recs:Seq[Recommendation])
//计算余弦相似度

object alsRecommend {
  def computeSim(m1: DoubleMatrix, m2:DoubleMatrix) : Double ={
    m1.dot(m2) / ( m1.norm2()  * m2.norm2() )
  }
  def main(args: Array[String]): Unit = {
    //创建一个SparkConf配置
    val sparkConf = new SparkConf().setAppName("ALsRecoomend").setMaster("local[2]").set("spark.executor.memory", "5G").set("spark.driver.memory", "3G")
    //.setIfMissing("spark.driver.host","115.200.36.247")
    //基于SparkConf创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    //创建一个MongoDBConfig
    val mongoConfig = MongoConfig( "mongodb://master01:27017/recommender", "reommender")
    //als模型参数
    val (rank, iterations, lambda) =(100,10,0.001)

    import spark.implicits._

    //读取mongoDB中的业务数据
    val rateRDD = spark
      .read
      .option("uri", "mongodb://master01:27017/recommender")
      .option("collection","Rating")
      .format("com.mongodb.spark.sql")
      .load()
      .as[MyRating]
      .rdd
      .map(rating => (rating.uid, rating.mid, rating.score)).cache()
    print("评分数据加载完成")
    val data = rateRDD.map(item => Rating(item._1, item._2, item._3))
    //训练ALS模型
    val model = ALS.train(data, rank, iterations, lambda)
    printf("训练als模型完成")
    //用户的数据集 RDD[Int]
    val uRDD = rateRDD.map(_._1).distinct().cache()

    //计算用户推荐矩阵

    //电影数据集 RDD[Int]
    val movieRDD = spark
      .read
      .option("uri", "mongodb://master01:27017/recommender")
      .option("collection","Movie")
      .format("com.mongodb.spark.sql")
      .load()
      .as[Movie]
      .rdd
      .map(_.mid).cache()
    print("电影数据加载完成")
    //创建训练数据集

    //需要构造一个usersProducts  RDD[(Int,Int)]
    val predata = uRDD.cartesian(movieRDD).cache()
    println("用户电影矩阵笛卡儿积完成")
    //
    val result = model.predict(predata).cache()
    printf("预测完成")
    val userRecs35 = result
      .filter(_.rating > 0)
      .map(item => (item.user, (item.product, item.rating)))
      .groupByKey()
      .map {
        case (uid, recs) => UserRecs(uid, recs.toList.sortWith(_._2 > _._2).take(10).map(x => Recommendation(x._1, x._2)))
      }.toDF().cache()
    print("计算用户推荐")
    userRecs35.write
      .option("uri", "mongodb://master01:27017/recommender")
      .option("collection","UserRecs")
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    print("执行1完了")
    //计算电影相似度矩阵
    val movieFeatures = model.productFeatures.map{case (mid,freatures) =>
      (mid, new DoubleMatrix(freatures))
    }

    val movieRecs = movieFeatures.cartesian(movieFeatures)
      .filter{case (a,b) => a._1 != b._1}
      .map{case (a,b) =>
        val simScore = this.computeSim(a._2,b._2)
        (a._1,(b._1,simScore))
      }.filter(_._2._2 > 0.7)
      .groupByKey()
      .map{ case (mid,items) =>
        MovieRecs(mid,items.toList.map(x => Recommendation(x._1,x._2)))
      }.toDF()

    movieRecs
      .write
      .option("uri", mongoConfig.uri)
      .option("collection","MovieRecs")
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()


    //关闭Spark
    spark.close()
  }


}
