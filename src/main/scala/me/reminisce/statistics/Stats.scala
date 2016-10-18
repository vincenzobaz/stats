package me.reminisce.statistics


import reactivemongo.bson._
import me.reminisce.server.GameEntities._
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.statistics.Utils._
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
    rivals: Set[String],
    questionsByType: QuestionsByType
    )
  
  case class QuestionStats(
    amount: Int,
    correct: Int,
    wrong: Int,
    avoid: Int
    )

  case class QuestionsByType(
    multipleChoice: QuestionStats,
    timeline: QuestionStats,
    geolocation: QuestionStats,
    order: QuestionStats,
    misc: QuestionStats
    )

  implicit val questionStatsHandler: BSONHandler[BSONDocument, QuestionStats] = Macros.handler[QuestionStats]  
  implicit val questionsByTypeHandler: BSONHandler[BSONDocument, QuestionsByType] = Macros.handler[QuestionsByType]  
   
  implicit object StatsReader extends BSONDocumentReader[StatsEntities]{
    def read(doc: BSONDocument): StatsEntities = {
      val id = doc.getAs[BSONObjectID]("_id").get
      val userId = doc.getAs[String]("userId").get
      val date = doc.getAs[DateTime]("date").get
      val amount = doc.getAs[Int]("amount").get
      val win = doc.getAs[Int]("win").get
      val lost = doc.getAs[Int]("lost").get
      val tie = doc.getAs[Int]("tie").get
      val rivals = doc.getAs[Set[String]]("rivals").get
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