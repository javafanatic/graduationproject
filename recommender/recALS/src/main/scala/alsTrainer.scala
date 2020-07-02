import breeze.numerics.sqrt
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object ALSTrain {
  // 输出最终的最优参数
  def printResult(userrec:RDD[Rating]): Unit ={
 //   val result = for(rank <- Array(5,8,10,15,20,30,50,70,90,100,120); lambda <- Array(10,5,3,2,1,0.5, 0.2,0.1, 0.001);iter<-Array(1,5,10,15,20,30,40,50))
 val result = for(iter<-Array(10/*,15,30,50*/);rank <- Array(10,20,30,50,80,120,200); lambda <- Array(5,2,1,0.5, 0.2,0.1,0.05, 0.001,0.00000001))
      yield {
        val model = ALS.train(userrec,rank,iter,lambda)
        val rmse = computeRmse(model,userrec)
        (iter,rank,lambda,rmse.formatted("%.5f"))
      }
    println("参数结果："+result)//.sortBy(_._3).head)
    result.foreach(item=>{
      println(item._1+"  "+item._2+" "+item._3+" "+item._4)
    })
  }

  def main(args: Array[String]): Unit = {
    //创建SparkConf,以双内核的方式运行
    val sparkConf = new SparkConf().setAppName("MyALSTrainer").setMaster("local[2]")
    print("config finish")
    //根据上面的配置创建SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    print("session finish")
    val mongoConfig = MongoConfig("mongodb://master01:27017/recommender","recommender")
    import spark.implicits._

    //加载评分数据
    val ratingRDD = spark
      .read
      .option("uri",mongoConfig.uri)
      .option("collection","Rating")
      .format("com.mongodb.spark.sql")
      .load()
      .as[MyRating]
      .rdd
      .map(rating => Rating(rating.uid,rating.mid,rating.score)).cache()
    print("rating finish")
    //输出最优参数
    printResult(ratingRDD)

    //关闭Spark
    spark.close()
  }


  def computeRmse(model:MatrixFactorizationModel, userrecs:RDD[Rating]):Double={
    //需要构造一个usersProducts  RDD[(Int,Int)]
    //原始的数据（用户产品=》评分）
    val old = userrecs.map(item => ((item.user,item.product),item.rating))
    //准备好用户推荐矩阵
    val data= userrecs.map(item => (item.user,item.product))
    //用训练好的模型根据用户
    val temp = model.predict(data)

    val predictResult = temp.map(item => ((item.user,item.product),item.rating))

    sqrt(
      old.join(predictResult).map{case ((uid,mid),(real,pre))=>
        // 真实值和预测值之间的两个差值
        (real - pre)*(real-pre)
      }.mean()
    )
  }

}
