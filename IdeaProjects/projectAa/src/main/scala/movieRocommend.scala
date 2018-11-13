import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

import scala.util.Random


object movieRocommend {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf().setMaster("local[5]").setAppName("movieRecommend")
    val sc = new SparkContext(conf)
    //        sc.setLogLevel("ERROR")

    /*val sqlContext = new SQLContext(sc)
    //读csv文件
    val data: DataFrame = sqlContext.read.format("com.databricks.spark.csv")
      .option("header", true) //在csv第一行有属性"true"，没有就是"false"
      .option("inferSchema", true) //这是自动推断属性列的数据类型
      .load("D:\\testFile\\ml-latest-small\\tags.csv")
    data.show()*/


    /* 步骤1：读取电影和评分的数据*/
    //读取信息到本地
    val movies = sc.textFile("E:\\dwg\\Fireflies\\useFiles\\ml-latest-small\\movies.csv").map { line =>
      val fields = line.split(",")
      (fields(0).toInt, fields(1))
    }.collect().toMap
    //读取评分数据为RDD
    val ratings = sc.textFile("E:\\dwg\\Fireflies\\useFiles\\ml-latest-small\\ratings.csv").map { line =>
      val fields = line.split(",")
      val rating = Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
      val timestamp = fields(3).toLong % 10
      (timestamp, rating)
    }
//    输出数据集基本信息
    val numRatings = ratings.count()
    val numUsers = ratings.map(_._2.user).distinct().count()
    val numMovies = ratings.map(_._2.product).distinct().count()
    println("Got " + numRatings + " ratings from " + numUsers + " users on " + numMovies + " movies")
    println()

//    获取电影id
    val mostRateMovieIds = ratings.map(_._2.product).countByValue()
      .toSeq.sortBy(-_._2).take(50).map(_._1)
    val random = new Random(0)
    val selectedMovies = mostRateMovieIds.filter(x => random.nextDouble() < 0.2)
      .map(x => (x, movies(x))).toSeq
    val myRatings = elicitateRatings(selectedMovies)
    val myRatinsRDD = sc.parallelize(myRatings)

    /*步骤2：训练集、验证集、测试集*/
    val numPartitions = 4
    val training = ratings.filter(x => x._1 < 6).values.union(myRatinsRDD).repartition(numPartitions).persist()
    val validation = ratings.filter(x => x._1 >= 6 && x._1 < 8).values.repartition(numPartitions).persist()
    val test = ratings.filter(x => x._1 >= 8).values.persist()
    val numTraining = training.count()
    val numValidation = validation.count()
    val numTest = test.count()
    println("Training: " + numTraining + ", validation: " + numValidation + ", test: " + numTest)
    println()

    /*步骤4：使用不同的参数训练协同过滤模型，并选出RMSE最小模型*/
    val ranks = List(8, 12)
    val lambdas = List(1.0, 10.0)
    val numIters = List(10, 20)
    var bestModel: Option[MatrixFactorizationModel] = None
    var bestValidationRmse = Double.MaxValue
    var bestRank = 0
    var bestLambda = -1.0
    var bestNumIter = -1
    for (rank <- ranks; lambda <- lambdas; numIter <- numIters) {
      val model = ALS.train(training, rank, numIter, lambda)
      val validationRmse = computeRmse(model, validation, numValidation)
      println("RMSE (validation)=" + validationRmse + " for the model trained with rand =" + rank
        + ", lambda=" + lambda + ", and numIter= " + numIter + ".")
      if (validationRmse < bestValidationRmse) {
        bestModel = Some(model)
        bestValidationRmse = validationRmse
        bestRank = rank
        bestLambda = lambda
        bestNumIter = numIter
      }
    }

    //用最佳模型预测测试集的评分，并计算和实际评分之间的均方根误差（RMSE）
    val testRmse = computeRmse(bestModel.get, test, numTest)
    println()
    println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda
      + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".")
    println()
    val meanRating = training.union(validation).map(_.rating).mean()
    val baselineRmse = math.sqrt(test.map(x => (meanRating - x.rating) * (meanRating - x.rating))
      .reduce(_ + _) / numTest)
    val improvement = (baselineRmse - testRmse) / baselineRmse * 100
    println("The best model improves the baseline by " + "%1.2f".format(improvement) + "%.")
    println()

    //推荐前十部感兴趣的电影，要剔除用户已经评分的电影
    val myRateMovieIds = myRatings.map(_.product).toSet
    val candidates = sc.parallelize(movies.keys.filter(!myRateMovieIds.contains(_)).toSeq)
    val recommendations = bestModel.get.predict(candidates.map((0, _)))
      .collect().sortBy(-_.rating).take(10)
    var i = 1
    println("movies recommended for you : ")
    recommendations.foreach { r =>
      println("%2d".format(i) + ": " + movies(r.product))
      i += 1
    }

    sc.stop()

  }

  /*步骤3：定义函数计算均方根误差RMSE*/
  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long): Double = {
    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    val predictionsAndRatings = predictions.map { x => ((x.user, x.product), x.rating) }
      .join(data.map(x => ((x.user, x.product), x.rating))).values
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / n)
  }


  def elicitateRatings(movies: Seq[(Int, String)]) = {
    val prompt = "Please rate the following movie (1-5 (best), or 0 if not seen):"
    println(prompt)
    val ratings = movies.flatMap { x =>
      var rating: Option[Rating] = None
      var valid = false
      while (!valid) {
        print(x._2 + ": ")
        try {
          val r = Console.readDouble()
          if (r < 0 || r > 5) {
            println(prompt)
          } else {
            valid = true
            if (r > 0) {
              rating = Some(Rating(0, x._1, r))
            }
          }
        } catch {
          case e: Exception => println(prompt)
        }
      }
      rating match {
        case Some(r) => Iterator(r)
        case None => Iterator.empty
      }
    }
    if (ratings.isEmpty) {
      error("No rating provided!")
    } else {
      ratings
    }
  }

}

