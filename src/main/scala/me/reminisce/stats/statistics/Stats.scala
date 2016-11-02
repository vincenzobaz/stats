package me.reminisce.stats.statistics


import reactivemongo.bson._
import me.reminisce.stats.server.GameEntities._
import me.reminisce.stats.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.stats.statistics.Utils._
import com.github.nscala_time.time.Imports._

object Stats {

  case class StatsEntities (
    id: BSONObjectID,
    userId: String,
    date: DateTime = DateTime.now,
    amount: Int,
    win: Int,
    lost: Int,  
    tie: Int,  
    rivals: Set[Rival],
    questionsByType: QuestionsByType
    )
  
  case class QuestionStats(
    amount: Int,
    correct: Double,
    wrong: Double,
    avoid: Int
    )

  case class Rival(
    rivalId: String,
    number: Int
    )

  case class QuestionsByType(
    multipleChoice: QuestionStats,
    timeline: QuestionStats,
    geolocation: QuestionStats,
    order: QuestionStats
    )

  implicit val questionStatsHandler: BSONHandler[BSONDocument, QuestionStats] = Macros.handler[QuestionStats]  
  implicit val questionsByTypeHandler: BSONHandler[BSONDocument, QuestionsByType] = Macros.handler[QuestionsByType]  
  implicit val rivalHandler: BSONHandler[BSONDocument, Rival] = Macros.handler[Rival]  
   

  implicit object StatsReader extends BSONDocumentReader[StatsEntities]{
    def read(doc: BSONDocument): StatsEntities = {
      val id = doc.getAs[BSONObjectID]("_id").get
      val userId = doc.getAs[String]("userId").get
      val date = doc.getAs[DateTime]("date").get
      val amount = doc.getAs[Int]("amount").get
      val win = doc.getAs[Int]("win").get
      val lost = doc.getAs[Int]("lost").get
      val tie = doc.getAs[Int]("tie").get
      val rivals = doc.getAs[Set[Rival]]("rivals").get
      val questionsByType = doc.getAs[QuestionsByType]("questionsByType").get
      StatsEntities(id, userId, date, amount, win, lost, tie, rivals, questionsByType)
    }
  }

  implicit object StatsWriter extends BSONDocumentWriter[StatsEntities]{
    def write(stats: StatsEntities): BSONDocument = {
      val StatsEntities(_id, userId, date, amount, win, lost, tie, rivals, questionsByType) = stats
      BSONDocument(
        "userId" -> userId,
        "date" -> date,
        "amount" -> amount,
        "win"-> win,
        "lost" -> lost,
        "tie" -> tie,
        "rivals" -> rivals,
        "questionsByType" -> questionsByType
        )
    }
  }
}