import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object wordCount {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf().setMaster("local[1]").setAppName("wordCount")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val data = sc.textFile("E:\\dwg\\Firefiles\\useFiles\\readme.txt")
    val wordcount = data.flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, false)
      .collect.foreach(println)


    sc.stop()


  }
}
