package me.reminisce

import org.scalatest.FunSuite
import me.reminisce.statistics.StatisticEntities._
import reactivemongo.bson._
import com.github.nscala_time.time.Imports._

class StatsBSONSerializersTest extends FunSuite {
 
 // ***** AverageScore *****
  val averageScore = AverageScore(2.3)
  val docAverageScore = BSONDocument(
    "average" -> 2.3)
  test("AverageScoreWrite"){
    val bson = BSON.writeDocument(averageScore)
    assert(bson == docAverageScore)
  }
  test("AverageScoreRead"){
    val result = docAverageScore.as[AverageScore]
    assert(result == averageScore)
  }
  test("AverageScoreWriteRead"){
    val bson = BSON.writeDocument(averageScore)
    val result = bson.as[AverageScore]
    assert(averageScore == result)
  }

 // ***** CountWinnerGame *****
  val countWin = CountWinnerGame(2, 6)
  val docCountWin = BSONDocument(
    "won" -> 2,
    "lost" -> 6)

  test("CountWinnerGameWrite"){
    val bson = BSON.writeDocument(countWin)
    assert(bson == docCountWin)
  }
  test("CountWinnerGameRead"){
    val result = docCountWin.as[CountWinnerGame]
    assert(result == countWin)
  }
  test("CountWinnerGameWriteRead"){
    val bson = BSON.writeDocument(countWin)
    val result = bson.as[CountWinnerGame]
    assert(countWin == result)
  }

 // ***** CountCorrectQuestion *****
  val countQuestion = CountCorrectQuestion(32, 49)
  val docCountQuestion = BSONDocument(
    "correct" -> 32,
    "wrong" -> 49)

  test("CountCorrectQuestionWrite"){
    val bson = BSON.writeDocument(countQuestion)
    assert(bson == docCountQuestion)
  }
  test("CountCorrectQuestionRead"){
    val result = docCountQuestion.as[CountCorrectQuestion]
    assert(result == countQuestion)
  }
  test("CountCorrectQuestionWriteRead"){
    val bson = BSON.writeDocument(countQuestion)
    val result = bson.as[CountCorrectQuestion]
    assert(countQuestion == result)
  }

  // ***** Stats *****

  val now: DateTime = DateTime.now
  println(s"now: $now")
  val stats = Stats("userID123", Some(countWin), Some(averageScore), Some(countQuestion), Some(now), Some("idStat456"))
  val docStats = BSONDocument(
    "_id" -> "idStat456",
    "userID" -> "userID123",
    "countWinnerGame" -> countWin,
    "averageScore" -> averageScore,
    "countCorrectQuestion" -> countQuestion,
    "computationTime" -> now
    )
 
  test("StatsWrite"){
    val bson = BSON.writeDocument(stats)
    assert(bson.getAs[String]("_id") == docStats.getAs[String]("_id"))
    assert(bson.getAs[String]("userID") == docStats.getAs[String]("userID"))
    assert(bson.getAs[CountWinnerGame]("countWinnerGame") == docStats.getAs[CountWinnerGame]("countWinnerGame"))
    assert(bson.getAs[AverageScore]("averageScore") == docStats.getAs[AverageScore]("averageScore"))
    assert(bson.getAs[CountCorrectQuestion]("countCorrectQuestion") == docStats.getAs[CountCorrectQuestion]("countCorrectQuestion"))
    println(s"bson: ${bson.getAs[DateTime]("computationTime")}")
    println(s"doc: ${docStats.getAs[DateTime]("computationTime")}")
    assert(bson.getAs[DateTime]("computationTime") == docStats.getAs[DateTime]("computationTime"))
   // assert(bson == docStats)
  }
  
  test("StatsRead"){
    val result = docStats.as[Stats]
    assert(result == stats)
  }
  test("StatsWriteRead"){
    val bson = BSON.writeDocument(stats)
    val result = bson.as[Stats]
    assert(stats == result) 
  }


}