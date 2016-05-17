package me.reminisce.statistics

import reactivemongo.bson._
import me.reminisce.server.GameEntities._
import com.github.nscala_time.time.Imports._
import me.reminisce.model.InsertionMessages._
import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities.QuestionsBreakDownKind.QuestionsBreakDownKind

object StatisticEntities {
/*
    ##### NEW API ########
*/
  
  case class StatResponse(userID: String, frequencies: FrequencyOfPlays) 
    extends RestMessage

  case class FrequencyOfPlays(
    days: StatsOnInterval, 
    week: StatsOnInterval, 
    month: StatsOnInterval, 
    year: StatsOnInterval, 
    allTime: StatsOnInterval
    )

  case class StatsOnInterval(
    ago: Int, 
    amount: Int, 
    corect: Int, 
    percentCorrect: Double, 
    questionsBreakDown: List[QuestionsBreakDown], 
    gamePslayedAgaints: List[GamesPlayedAgainst]
    )

  case class GamesPlayedAgainst(
    userID: String,
    numberOfGames: Int,
    won: Int,
    lost: Int
    )

  case class QuestionsBreakDown(
    questionBDtype: QuestionsBreakDownKind,
    totalAmount: Int,
    correct: Int,
    percentCorrect: Double
    )

  object QuestionsBreakDownKind extends Enumeration {
    type QuestionsBreakDownKind = Value
    val MC = Value("MC")
    val TL = Value("TL ")
    val GEO = Value("GEO")
    val ORD = Value("ORD")
  } 
/*
    ######################
*/
  sealed trait Statistic  
  
  case class CountWinnerGame(won: Int, lost: Int) extends Statistic
  case class AverageScore(average: Double) extends Statistic
  case class CountCorrectQuestion(correct: Int, wrong: Int) extends Statistic

  /*
  Stats to compute:
  - number of game played -> CountWinnerGame
  - number of question answered -> CountCorrectQuestion
  - score average -> AverageScore
  - Best score
  - ... ?
  */

  case class Stats(
                    userID: String,
                    countWinnerGame: Option[CountWinnerGame],
                    averageScore: Option[AverageScore],
                    countCorrectQuestion: Option[CountCorrectQuestion],
                    computationDate: Option[DateTime] = Some(DateTime.now),
                    _id: Option[String] = None
    ) extends Statistic with Entity

  implicit val gameResumeHandler: BSONHandler[BSONDocument, CountWinnerGame] = Macros.handler[CountWinnerGame]
  implicit val avgScoreHandler: BSONHandler[BSONDocument, AverageScore] = Macros.handler[AverageScore]
  implicit val questionResumeHandler: BSONHandler[BSONDocument, CountCorrectQuestion] = Macros.handler[CountCorrectQuestion]
 
  implicit object DatetimeReader extends BSONReader[BSONDateTime, DateTime]{
      def read(bson: BSONDateTime): DateTime = {
        val time = new DateTime(bson.value)
        println(s"Read: $time")
        time
      }
  }

  implicit object DatetimeWriter extends BSONWriter[DateTime, BSONDateTime]{
      def write(t: DateTime): BSONDateTime = {
        println(s"write: ${t.getMillis}")
        BSONDateTime(t.getMillis)
      }
}

  implicit val StatWriter: BSONDocumentWriter[Stats] = Macros.writer[Stats]

  implicit object StatsWriter extends BSONDocumentWriter[Statistic with Entity] {
    def write(stats: Statistic with Entity): BSONDocument =
      stats match {
        case Stats(userID, countWinnerGame, averageScore, countCorrectQuestion, time, id) =>
      BSONDocument(
        "_id" -> id,
        "userID" -> userID,
        "countWinnerGame" -> countWinnerGame,
        "averageScore" -> averageScore,
        "countCorrectQuestion" -> countCorrectQuestion,
        "computationTime" -> time
      )
    }
  }

  implicit object StatsReader extends BSONDocumentReader[Stats] {
    def read(doc: BSONDocument): Stats = {
      
      val id = doc.getAs[String]("_id")
      println(id) // TODO Why it is None ??!

      val userID = doc.getAs[String]("userID").get      
      val countWinnerGame = doc.getAs[CountWinnerGame]("countWinnerGame")
      val averageScore = doc.getAs[AverageScore]("averageScore")
      val countCorrectQuestion = doc.getAs[CountCorrectQuestion]("countCorrectQuestion")
      val computationTime = doc.getAs[DateTime]("computationTime")
      println("Time in statreader "+computationTime)
      Stats(userID, countWinnerGame, averageScore, countCorrectQuestion, computationTime, id)
    }
  }

  implicit object StatisticWriter extends BSONDocumentWriter[Statistic] {
    def write(stat: Statistic): BSONDocument =
      stat match {
        case CountWinnerGame(won, lost) => BSONDocument(
          "won" -> won,
          "lost" -> lost
          )
        case AverageScore(average) => BSONDocument(
          "average" -> average
          )
        case CountCorrectQuestion(correct, wrong) => BSONDocument(
          "correct" -> correct,
          "wrong" -> wrong
          )
        case a: Stats => StatsWriter.write(a)
      }
  }

  }