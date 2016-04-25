package me.reminisce.statistics

import reactivemongo.bson._
import me.reminisce.server.GameEntities._

object StatisticEntities {

  abstract sealed trait Statistic
  
  case class GameResume(won: Int, lost: Int) extends Statistic
  case class AverageScore(average: Double) extends Statistic
  case class QuestionResume(correct: Int, wrong: Int) extends Statistic
  //list of all players ?
  case class Stats(_id: String,
    gameResume: GameResume,
    averageScore: AverageScore,
    questionResume: QuestionResume
    ) extends EntityMessage with Statistic

  implicit val gameResumeHandler: BSONHandler[BSONDocument, GameResume] = Macros.handler[GameResume]
  implicit val avgScoreHandler: BSONHandler[BSONDocument, AverageScore] = Macros.handler[AverageScore]
  implicit val questionResumeHandler: BSONHandler[BSONDocument, QuestionResume] = Macros.handler[QuestionResume]
  implicit val statsHandler: BSONHandler[BSONDocument, Stats] = Macros.handler[Stats]

  implicit object StatisticWriter extends BSONDocumentWriter[Statistic] {
    def write(stat: Statistic) : BSONDocument =
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
        case Stats(id, wonGame, averageScore, totalQuestion) => BSONDocument(
          "_id" -> id,
          "wonGame" -> wonGame,
          "averageScore" -> averageScore,
          "totalQuestion" -> totalQuestion
          )
      }
  }

  }