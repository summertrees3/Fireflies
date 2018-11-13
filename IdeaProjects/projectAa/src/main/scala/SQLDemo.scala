
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


object SQLDemo {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName("SQLDemo").setMaster("local[2]")
    val sc=new SparkContext(conf)
    val sqlContext=new SQLContext(sc)
    val personRdd=sc.textFile("E:\\dwg\\Firefiles\\useFiles\\person.txt").map(line =>{
      val fields=line.split(",")
      Person(fields(0).toInt,fields(1),fields(2).toInt)
    })

    import sqlContext.implicits._
    val personDf=personRdd.toDF
    personDf.registerTempTable("t_person")
    sqlContext.sql("select * from t_person").show()

    sc.stop()
  }
}
case class Person(id:Int,name:String,age:Int)
