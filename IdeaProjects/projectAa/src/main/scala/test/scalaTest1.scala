package test

object scalaTest1 {

  def main(args: Array[String]): Unit = {

    //1
//    def formatArgs(args: Array[String]) = args.mkString("\n")
//    println(formatArgs(Array("zero","one","two")))


    //柯里化
    def curriedSum(x:Int)(y:Int)=x+y
    println(curriedSum(1)(2))


    //2


  }
}
