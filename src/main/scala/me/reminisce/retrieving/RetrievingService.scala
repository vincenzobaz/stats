package me.reminisce.retrieving

import akka.actor._
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.model.Messages._
import reactivemongo.api.DefaultDB
import com.github.nscala_time.time.Imports._
import reactivemongo.bson.{BSONDocument, BSONDateTime}
import reactivemongo.api.collections.bson._
import me.reminisce.statistics.Stats._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object RetrievingService {
  def props(database: DefaultDB):Props =
    Props(new RetrievingService(database))
}

class RetrievingService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = waitingForMessages
 
  def waitingForMessages: Receive = {
    case RetrieveStats(userId, from, to, limit) =>
      val client = sender
      val future = getStatistics(userId, from, to, limit)
      future.onComplete{
        case Success(stats) =>
          if(stats.isEmpty){
            client ! UserNotFound(s"Statistics not found for $userId")
          } else {
            client ! StatsRetrieved(stats)
          }
        case Failure(error) =>
          sender !  Abort
      }      
    case o => log.info(s"[RS] Unexpected message ($o) received in waitingForMessages state")
  }

  def getQuery(userId: String, from: Option[DateTime], to: Option[DateTime]) : BSONDocument =  {
    (from, to) match {
      case (Some(f), Some(t)) => 
        BSONDocument(
          "userId" -> userId,
          "date" -> BSONDocument(
            "$gte" -> BSONDateTime(f.getMillis),
            "$lte" -> BSONDateTime(t.getMillis)
          )
        )
      case (Some(f), None) =>
        BSONDocument(
          "userId" -> userId,
          "date" -> BSONDocument(
            "$gte" -> BSONDateTime(f.getMillis)
        )
      )    
      case (None, Some(t)) =>
        BSONDocument(
          "userId" -> userId,
          "date" -> BSONDocument(
            "$lte" -> BSONDateTime(t.getMillis)
          )
        )
      case (None, None) =>
        BSONDocument(
          "userId" -> userId
        )
    }
  }

  def getStatistics(userId: String, from: Option[DateTime], to: Option[DateTime], limit: Option[Int]) : Future[List[StatsEntities]] = {
    val query = getQuery(userId, from, to)
    println(query)
    val collectionStats = database[BSONCollection](DatabaseCollection.statsCollection)
    limit match {
      case Some(max) =>
        collectionStats.find(query).sort(BSONDocument("date" -> -1)).cursor[StatsEntities]().collect[List](max)  
      case None =>
        collectionStats.find(query).sort(BSONDocument("date" -> -1)).cursor[StatsEntities]().collect[List]()

    }
  }
}
