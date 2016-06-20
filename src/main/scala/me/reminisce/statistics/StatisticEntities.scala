package me.reminisce.statistics

import reactivemongo.bson._
import me.reminisce.statistics.StatisticEntities.QuestionsBreakDownKind.QuestionsBreakDownKind
import com.github.nscala_time.time.Imports._

object StatisticEntities {

  case class StatResponse(
    userID: String, 
    frequencies: FrequencyOfPlays,
    computationTime: DateTime = DateTime.now
    )

  case class FrequencyOfPlays(
    day: List[StatsOnInterval] = List(), 
    week: List[StatsOnInterval] = List(), 
    month: List[StatsOnInterval] = List(), 
    year: List[StatsOnInterval] = List(), 
    allTime: Option[List[StatsOnInterval]] = None
    )

  case class StatsOnInterval(
    ago: Int, 
    amount: Int, 
    won: Int, 
    lost: Int, 
    questionsBreakDown: List[QuestionsBreakDown], 
    gamesPlayedAgainst: List[GamesPlayedAgainst]
    )

  case class GamesPlayedAgainst(
    userID: String,
    numberOfGames: Int,
    won: Int,
    lost: Int
    )

  case class QuestionsBreakDown(
    questionsBreakDownKind: QuestionsBreakDownKind,
    totalAmount: Int,
    correct: Int,
    percentCorrect: Double
    )
// TODO use the QuestionKind in GameEntities
  object QuestionsBreakDownKind extends Enumeration {
    type QuestionsBreakDownKind = Value
    val MultipleChoice = Value("MultipleChoice")
    val Timeline = Value("Timeline")
    val Geolocation = Value("Geolocation")
    val Order = Value("Order")
    val Misc = Value("Misc")
  } 

  implicit object DatetimeReader extends BSONReader[BSONDateTime, DateTime]{
    def read(bson: BSONDateTime): DateTime = {
      val time = new DateTime(bson.value)
      time
    }
  }

  implicit object DatetimeWriter extends BSONWriter[DateTime, BSONDateTime]{
    def write(t: DateTime): BSONDateTime = {
      BSONDateTime(t.getMillis)
    }
  }

  implicit object QuestionsBreakDownReader extends BSONDocumentReader[QuestionsBreakDown]{
    def read(doc: BSONDocument) : QuestionsBreakDown = {
      val questionBDKind = QuestionsBreakDownKind.withName(doc.getAs[String]("questionsBreakDownKind").get)
      val totalAmount = doc.getAs[Int]("totalAmount").get
      val correct = doc.getAs[Int]("correct").get
      val percentCorrect = doc.getAs[Double]("percentCorrect").get
      QuestionsBreakDown(questionBDKind, totalAmount, correct, percentCorrect)
    }
  }

  implicit object QuestionsBreakDownWriter extends BSONDocumentWriter[QuestionsBreakDown]{
    def write(question: QuestionsBreakDown): BSONDocument = {
      val QuestionsBreakDown(kind, total, correct, percent) = question
      BSONDocument(
      "questionsBreakDownKind" -> kind.toString,
      "totalAmount" -> total,
      "correct" -> correct,
      "percentCorrect" -> percent
      )
    }
  }

  implicit val gamesPlayedAgainstHandler: BSONHandler[BSONDocument, GamesPlayedAgainst] = Macros.handler[GamesPlayedAgainst]  
  implicit val statsOnIntervalHandler: BSONHandler[BSONDocument, StatsOnInterval] = Macros.handler[StatsOnInterval]
  implicit val frequencyOfPlaysHandler: BSONHandler[BSONDocument, FrequencyOfPlays] = Macros.handler[FrequencyOfPlays]
  implicit val StatResponseHandler: BSONHandler[BSONDocument, StatResponse] = Macros.handler[StatResponse]
  
  implicit object StatsHandlerReader extends BSONDocumentReader[StatResponse]{
    def read(doc: BSONDocument) : StatResponse = {
      val id = doc.getAs[String]("userID").get
      val freq = doc.getAs[FrequencyOfPlays]("frequencies").get
      val time = doc.getAs[DateTime]("computationTime").get
      StatResponse(id, freq, time)
    }
  }

  object IntervalKind extends Enumeration {
    type IntervalKind = Value
    val daily = Value("DAY")
    val weekly = Value("WEEK")
    val monthly = Value("MONTH")
    val yearly = Value("YEAR")
    val allTime = Value("ALLTIME")
  } 
}
