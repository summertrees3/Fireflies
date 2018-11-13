import scala.util.Random

/*
object matchCase {

  def main(args: Array[String]): Unit = {

    val arr = Array("jack", "tom", "jerry")
    val name = arr(Random.nextInt(arr.length))
    name match {
      case "tom" => println("1号")
      case "jerry" => println("2号")
      case _ => println("其他")

    }

  }
}
*/


object CaseDemo extends App{

  /*
  val arr = Array(1, 3, 5)
  arr match {
    case Array(1, x, y) => println(x + " " + y)
    case Array(1) => println("only 1")
    case Array(1, _*) => println("1 ...")
    case _ => println("something else")
  }
  */

  val tup = (2, 3, 7)
  tup match {
    case (2, x, y) => println(s"1, $x, $y")
    case (_, z, 5) => println(z)
    case  _ => println("else")
  }
}


