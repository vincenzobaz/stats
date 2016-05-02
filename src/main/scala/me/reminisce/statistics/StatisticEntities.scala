package me.reminisce.statistics

import reactivemongo.bson._
import me.reminisce.server.GameEntities._

object StatisticEntities {

  sealed trait Statistic
  
  case class CountWinnerGame(won: Int, lost: Int) extends Statistic
  case class AverageScore(average: Double) extends Statistic
  case class CountCorrectQuestion(correct: Int, wrong: Int) extends Statistic

  // TODO Option[] for each field excepting the userID
  // TODO Add a field "Date"

  case class Stats(
                    userID: String,
                    countWinnerGame: CountWinnerGame,
                    averageScore: AverageScore,
                    countCorrectQuestion: CountCorrectQuestion,
                    _id: Option[BSONObjectID] = None
    ) extends Statistic with EntityMessage

  implicit val gameResumeHandler: BSONHandler[BSONDocument, CountWinnerGame] = Macros.handler[CountWinnerGame]
  implicit val avgScoreHandler: BSONHandler[BSONDocument, AverageScore] = Macros.handler[AverageScore]
  implicit val questionResumeHandler: BSONHandler[BSONDocument, CountCorrectQuestion] = Macros.handler[CountCorrectQuestion]
  implicit val statsHandler: BSONHandler[BSONDocument, Stats] = Macros.handler[Stats]

  implicit object StatsWriter extends BSONDocumentWriter[Statistic with EntityMessage] {
    def write(stats: Statistic with EntityMessage): BSONDocument =
      stats match {
        case Stats(userID, countWinnerGame, averageScore, countCorrectQuestion, id) =>
      BSONDocument(
        //"_id" -> id,
        "userID" -> userID,
        "countWinnerGame" -> countWinnerGame,
        "averageScore" -> averageScore,
        "countCorrectQuestion" -> countCorrectQuestion
      )
    }
  }
  implicit object StatsReader extends BSONDocumentReader[Stats] {
    def read(doc: BSONDocument): Stats = {
      val userID = doc.getAs[String]("userID").get
      val countWinnerGame = doc.getAs[CountWinnerGame]("countWinnerGame").get
      val averageScore = doc.getAs[AverageScore]("averageScore").get
      val countCorrectQuestion = doc.getAs[CountCorrectQuestion]("countCorrectQuestion").get
      Stats(userID, countWinnerGame, averageScore, countCorrectQuestion)
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