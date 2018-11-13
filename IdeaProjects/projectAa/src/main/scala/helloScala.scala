/*
*
* */

object helloScala {
  def main(args: Array[String]): Unit = {

    println("hello scala!")

    val c = for (i <- 1 to 10) yield i * 10
      println(c)


    val arr1=Array(1,2,3,4,5,6,7,8,9)
    val arr2=for(e <- arr1 if e%2==0 ) yield e*2
    println(arr2.toString)



    arr1.sortWith(_>_).map(x => print(x))

  }

}
