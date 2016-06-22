package me.reminisce.model

import org.scalatest.FunSuite
import me.reminisce.statistics.StatisticEntities._
import reactivemongo.bson._
import com.github.nscala_time.time.Imports._

class StatsBSONSerializersTest extends FunSuite {

  // ***** QuestionBreakDown *****
  val totalAmount = 30
  val correct = 15
  val percentCorrect = 0.5
  val qbd = QuestionsBreakDown(QuestionsBreakDownKind.MultipleChoice, totalAmount, correct, percentCorrect)
  val docQBD = BSONDocument(
      "questionsBreakDownKind" -> QuestionsBreakDownKind.MultipleChoice.toString,
      "totalAmount" -> totalAmount,
      "correct" -> correct,
      "percentCorrect" -> percentCorrect
      )

  test("QuestionsBreakDown Write") {
    val bson = BSON.writeDocument(qbd)
    assert(bson.getAs[String]("questionsBreakDownKind") == docQBD.getAs[String]("questionsBreakDownKind"))
    assert(bson.getAs[Int]("totalAmount") == docQBD.getAs[Int]("totalAmount"))
    assert(bson.getAs[Int]("correct") == docQBD.getAs[Int]("correct"))
    assert(bson.getAs[Double]("percentCorrect") == docQBD.getAs[Double]("percentCorrect"))
  }  
  test("QuestionsBreakDown Read") {
    val result = docQBD.as[QuestionsBreakDown]
    assert(result == qbd)
  }
  test("QuestionsBreakDown Write-Read"){
    val bson = BSON.writeDocument(qbd)
    val result = bson.as[QuestionsBreakDown]
    assert(qbd == result)
  }
  // ***** GamesPlayedAgainst *****
  val userID = "abc123,o&à"
  val numberOfGames = 14
  val won = 10
  val lost = 4
  val game = GamesPlayedAgainst(userID, numberOfGames, won, lost)
  val docGame = BSONDocument(
      "userID" -> userID,
      "numberOfGames" -> numberOfGames,
      "won" -> won,
      "lost" -> lost
    )

  test("GamesPlayedAgainst Write") {
    val bson = BSON.writeDocument(game)
    assert(bson.getAs[String]("numberOfGames") == docGame.getAs[String]("numberOfGames"))
    assert(bson.getAs[Int]("totalAmount") == docGame.getAs[Int]("totalAmount"))
    assert(bson.getAs[Int]("won") == docGame.getAs[Int]("won"))
    assert(bson.getAs[Int]("lost") == docGame.getAs[Int]("lost"))
   }  
  test("GamesPlayedAgainst Read") {
    val result = docGame.as[GamesPlayedAgainst]
    assert(result == game)
  }
  test("GamesPlayedAgainst Write-Read"){
    val bson = BSON.writeDocument(game)
    val result = bson.as[GamesPlayedAgainst]
    assert(game == result)
  }
  // ***** StatsOnInterval *****

  val ago = 2
  val amount = 4
  val questionsBreackDown = List(qbd, qbd)
  val gamePlayedAgainst = List(game, game, game)
  val statsOnInterval = StatsOnInterval(ago, amount, won, lost, questionsBreackDown, gamePlayedAgainst)
  val docStatsOnInterval = BSONDocument(
    "ago" -> ago,
    "amount" -> amount,
    "won" -> won,
    "lost" -> lost,
    "questionsBreakDown" -> questionsBreackDown,
    "gamesPlayedAgainst" -> gamePlayedAgainst
    )

  test("StatsOnInterval Write") {
    val bson = BSON.writeDocument(statsOnInterval)
    
    assert(bson.getAs[Int]("ago") == docStatsOnInterval.getAs[Int]("ago"))
    assert(bson.getAs[Int]("amount") == docStatsOnInterval.getAs[Int]("amount"))
    assert(bson.getAs[Int]("won") == docStatsOnInterval.getAs[Int]("won"))
    assert(bson.getAs[Int]("lost") == docStatsOnInterval.getAs[Int]("lost"))
    
    val questionWritten = bson.getAs[List[QuestionsBreakDown]]("questionsBreakDown")
    val questionGiven = docStatsOnInterval.getAs[List[QuestionsBreakDown]]("questionsBreakDown")
    assert(questionWritten.get.length == questionGiven.get.length)
    assert(questionWritten == questionGiven)
    
    val gamesPlayedWritten = bson.getAs[List[GamesPlayedAgainst]]("gamesPlayedAgainst")
    val gamesPlayedGiven = docStatsOnInterval.getAs[List[GamesPlayedAgainst]]("gamesPlayedAgainst")
    assert(gamesPlayedWritten == gamesPlayedGiven)
   }  

  test("StatsOnInterval Read") {
    val result = docStatsOnInterval.as[StatsOnInterval]
    assert(result == statsOnInterval)
  }
  test("StatsOnInterval Write-Read"){
    val bson = BSON.writeDocument(statsOnInterval)
    val result = bson.as[StatsOnInterval]
    assert(statsOnInterval == result)
  }
  // ***** FrequencyOfPlays *****

  val frequencyOfPlays = FrequencyOfPlays(List(statsOnInterval), List(), List(), List(statsOnInterval), Some(List(statsOnInterval)))
  val docFrequency = BSONDocument(
    "day" -> List(statsOnInterval),
    "week" -> List[StatsOnInterval](),
    "month" -> List[StatsOnInterval](),
    "year" -> List(statsOnInterval),
    "allTime" -> List(statsOnInterval)
    )

  test("FrequencyOfPlays Write") {
    val bson = BSON.writeDocument(frequencyOfPlays)
    
    val testElem: (String => Unit) = (elem: String) => {
      val written = bson.getAs[List[StatsOnInterval]](elem)
      val given = docFrequency.getAs[List[StatsOnInterval]](elem)
      (written, given) match {
        case (Some(wrt: List[StatsOnInterval]), Some(gvn: List[StatsOnInterval])) =>

          assert(wrt == gvn) 
        case (None, None) =>
          assert(true)
        case _ => fail()
      } 
    }
    val list = List("day", "week", "month", "year", "allTime")
    list.map(testElem)    
 }  

  test("FrequencyOfPlays Read") {
    val result = docFrequency.as[FrequencyOfPlays]
    assert(result == frequencyOfPlays)
  }
  test("FrequencyOfPlays Write-Read"){
    val bson = BSON.writeDocument(frequencyOfPlays)
    val result = bson.as[FrequencyOfPlays]
    assert(frequencyOfPlays == result)
  }

    // ***** StatResponse *****
    val date = DateTime.now
    val statResponse = StatResponse(userID, frequencyOfPlays, date)
    val docStatRep = BSONDocument(
      "userID" -> userID,
      "frequencies" -> frequencyOfPlays,
      "computationTime" -> date
      )

  test("StatResponse Write") {
    val bson = BSON.writeDocument(statResponse)
    assert(bson.getAs[String]("userID") == docStatRep.getAs[String]("userID"))
    assert(bson.getAs[FrequencyOfPlays]("frequencies") == docStatRep.getAs[FrequencyOfPlays]("frequencies"))
  }  
  test("StatResponse Read") {
    val result = docStatRep.as[StatResponse]
    assert(result == statResponse)
  }
  test("StatResponse Write-Read"){
    val bson = BSON.writeDocument(statResponse)
    val result = bson.as[StatResponse]
    assert(statResponse == result)
  } 
}
