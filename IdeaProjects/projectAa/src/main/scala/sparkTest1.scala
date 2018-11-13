import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object sparkTest1 {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)


//    val conf = new SparkConf().setMaster("local[5]").setAppName("movieRecommend")
//    val sc = new SparkContext(conf)
//    val sqlContext = new SQLContext(sc)
//    val df = sqlContext.range(0, 10)
//    df.show()


    /*SparkSession是spark2.x版本中推行的统一的spark入口*/
    val spark = SparkSession.builder().appName("SparkSessionApp")
      .master("local[2]").getOrCreate()
    val df = spark.range(0,10)
    df.show()

    spark.stop()
  }
}
