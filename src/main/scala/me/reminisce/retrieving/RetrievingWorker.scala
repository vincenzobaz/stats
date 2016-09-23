package me.reminisce.retrieving

import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.server.GameEntities._
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray, BSONString}
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack
import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.nscala_time.time.Imports._

object RetrievingWorker {
  def props(database: DefaultDB):Props =
    Props(new RetrievingWorker(database))
}

class RetrievingWorker(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = waitingForRequest
  /*
   * Wait for request
   */
  def waitingForRequest: Receive = {
    case RetrieveLastStatistics(userID) => 
      retrieveStats(userID, sender)
    case GetFirstPlayDate(userID) =>
      getFirstPlayDate(userID, sender)
    case o => log.info(s"Unexpected message ($o) received in RetrievingWorker")
  }

  /*
   * Query the date of the first play of a user and send it back to the client
   */
  def getFirstPlayDate(userID: String, client: ActorRef) = {
    import me.reminisce.model.DatabaseCollection
    
    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                )
              )

    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).sort(
        BSONDocument("creationTime" -> 1)).cursor[Game]().collect[List](1)

      s.onComplete{
        case Success(games)  =>
          if(!games.isEmpty) {
            val millis = games.head.creationTime
            client ! FirstPlayDate(millis.toDateTime)
          } else {
            client ! UserNotFound(s"No game found for $userID")
          }
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          client ! UserNotFound(s"No game found for $userID")
    }
  }

  /*
  * Query the last Statistics Entity for the userID and send it back to the client
  */
  def retrieveStats(userID: String, client: ActorRef): Unit = {
    import me.reminisce.model.DatabaseCollection

    val query = BSONDocument(
      "userID" -> userID
      )

    val s: Future[List[StatResponse]] = database[BSONCollection](
        DatabaseCollection.cacheCollection).find(query).sort(
        BSONDocument("computationTime" -> -1)).cursor[StatResponse]().collect[List](1)

      s.onComplete{
        case Success(stats)  =>
          if(!stats.isEmpty) {
            client ! StatisticsRetrieved(stats.head)
          } else {
            client ! StatisticsNotFound(s"No statistics found for $userID")
          }
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          client ! StatisticsNotFound(s"No statistics found for $userID")
    }
  }

}
