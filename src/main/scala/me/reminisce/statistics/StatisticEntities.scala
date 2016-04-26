package me.reminisce.statistics

import reactivemongo.bson._

import me.reminisce.server.GameEntities._

object StatisticEntities {

  abstract sealed trait Statistic
  
  case class GameResume(won: Int, lost: Int) extends Statistic
  case class AverageScore(average: Double) extends Statistic
  case class QuestionResume(correct: Int, wrong: Int) extends Statistic
  
  // TODO Option[] for each field excepting the ID
  case class Stats(
    userID: String,
    gameResume: GameResume,
    averageScore: AverageScore,
    questionResume: QuestionResume,
    _id: Option[BSONObjectID] = None
    ) extends Statistic with EntityMessage

  implicit val gameResumeHandler: BSONHandler[BSONDocument, GameResume] = Macros.handler[GameResume]
  implicit val avgScoreHandler: BSONHandler[BSONDocument, AverageScore] = Macros.handler[AverageScore]
  implicit val questionResumeHandler: BSONHandler[BSONDocument, QuestionResume] = Macros.handler[QuestionResume]
  implicit val statsHandler: BSONHandler[BSONDocument, Stats] = Macros.handler[Stats]

  implicit object StatsWriter extends BSONDocumentWriter[Statistic with EntityMessage] {
    def write(stats: Statistic with EntityMessage): BSONDocument =
      stats match {
        case Stats(userID, gameResume, averageScore, questionResume, id) =>
      BSONDocument(
        //"_id" -> id,
        "userID" -> userID,
        "gameResume" -> gameResume,
        "averageScore" -> averageScore,
        "questionResume" -> questionResume
      )
    }
  }
  implicit object StatsReader extends BSONDocumentReader[Stats] {
    def read(doc: BSONDocument): Stats = {
      val userID = doc.getAs[String]("userID").get
      val gameResume = doc.getAs[GameResume]("gameResume").get
      val averageScore = doc.getAs[AverageScore]("averageScore").get
      val questionResume = doc.getAs[QuestionResume]("questionResume").get
      Stats(userID, gameResume, averageScore, questionResume)
    }
  }

  implicit object StatisticWriter extends BSONDocumentWriter[Statistic] {
    def write(stat: Statistic): BSONDocument =
      stat match {
        case GameResume(won, lost) => BSONDocument(
          "won" -> won,
          "lost" -> lost
          )
        case AverageScore(average) => BSONDocument(
          "average" -> average
          )
        case QuestionResume(correct, wrong) => BSONDocument(
          "correct" -> correct,
          "wrong" -> wrong
          )
        case a: Stats => StatsWriter.write(a)
      }
  }

  }