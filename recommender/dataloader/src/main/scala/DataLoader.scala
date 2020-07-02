import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

//电影类
case class Movie(val mid: Int, val name: String, val video: String, val spic : String, val mpic :String , val lpic:String, val year: String,
                 val actors: String,   val country: String,val summary:String ,val language: String,   val directors: String,val writers: String,val types:String)
//评分类
case class Rating(val uid: Int, val mid: Int, val score: Double, val timestamp: Int)
//case class Tag(val uid: Int, val mid: Int, val tag: String, val timestamp: Int)

//评论类
case class Comment(val mid: Int, val uid: Int, val score: Double, val timestamp: Int,val comContents:String)
//mongodb配置
case class MongoConfig(val uri: String, val db: String)

// 数据的主加载服务
object DataLoader {

  // 程序的入口
  def main(args: Array[String]): Unit = {
    //严重
    print("hello2")
    // 需要创建一个SparkConf配置
    val sparkConf = new SparkConf().setAppName("DataLoader").setMaster("local[*]")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    // 将Movie数据集加载进去（）
    val mvRDD = spark.sparkContext.textFile("E:\\project\\bishe\\recommender\\dataloader\\src\\main\\resources\\movie66.csv")
    //将Movie的RDD装换为DataFrame,字符串去空格，id转为int
    val mvDF =mvRDD.map(item => {
      //csv分隔符设为^
      val attr = item.split("\\^")
      Movie(attr(0).toInt, attr(1).trim, attr(2).trim, attr(3).trim, attr(4).trim, attr(5).trim, attr(6).trim,
        attr(7).trim, attr(8).trim, attr(9).trim,attr(10).trim,attr(11).trim,attr(12).trim,attr(13).trim)
    }).toDF()
      //将评分数据集加载进去
    val rateRDD = spark.sparkContext.textFile("E:\\project\\bishe\\recommender\\dataloader\\src\\main\\resources\\ratings.csv")
    //将ratingRDD转换为DataFrame
    val rateDF = rateRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()
    //将评论数据集加载进去
    val commentRDD =spark.sparkContext.textFile("E:\\project\\bishe\\recommender\\dataloader\\src\\main\\resources\\comment64.csv")

    val commentDF=commentRDD.map(item =>{
      val attr=item.split("\\^")
      //因为爬到的数据里面最后的评论可能为空,因此要判断一下
      if(attr.length==5){
        Comment(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt,attr(4))
      }
      else{
        Comment(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt,"无")
      }

    }).toDF()


    implicit val mongoConfig = MongoConfig("mongodb://master01:27017/recommender", "recommender")

    // 需要将数据保存到MongoDB中
    storeDataInMongoDB( mvDF, rateDF,commentDF)
    //关闭spark.
    spark.stop()
  }

  // 将数据保存到MongoDB中的方法
  def storeDataInMongoDB(movieDF: DataFrame, ratingDF: DataFrame, commentDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {

    //新建一个到MongoDB的连接
    val mongosession = MongoClient(MongoClientURI(mongoConfig.uri))
    //如果MongoDB中有对应的数据库，那么应该删除
    mongosession(mongoConfig.db)("Movie").dropCollection()
    mongosession(mongoConfig.db)("Rating").dropCollection()


    //将当前数据写入到MongoDB(以spark)
    movieDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", "Movie")
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    ratingDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", "Rating")
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()


    commentDF
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", "Comment")
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    //对数据表建索引
    mongosession(mongoConfig.db)("Movie").createIndex(MongoDBObject("mid" -> 1))
    mongosession(mongoConfig.db)("Rating").createIndex(MongoDBObject("uid" -> 1))
    mongosession(mongoConfig.db)("Rating").createIndex(MongoDBObject("mid" -> 1))
    mongosession(mongoConfig.db)("Comment").createIndex(MongoDBObject("uid" -> 1))
    mongosession(mongoConfig.db)("Comment").createIndex(MongoDBObject("mid" -> 1))

    //关闭MongoDB的连接
    mongosession.close()
  }


}
