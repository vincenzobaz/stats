package me.reminisce.retrieving

import akka.actor._
import me.reminisce.server.domain.RestMessage
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.statistics.StatisticEntities._

import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.api.commands.bson
import reactivemongo.bson.{BSONDocument, BSONArray, BSONString}
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack
import scala.concurrent.Future
import scala.util.{Failure, Success}
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
    val queryBirthday = 
      BSONDocument(
        "aggregate" -> DatabaseCollection.gameCollection,
        "pipeline" -> BSONArray(
            BSONDocument(
              "$match" -> BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                ) 
              )
            ),
            BSONDocument(
              "$group" -> BSONDocument(
                "_id" -> userScore,
                "birthday" -> BSONDocument("$min" -> "$creationTime"),
                "test" -> BSONDocument("$max" -> BSONString("$creationTime")),
                "count" -> BSONDocument("$sum" -> 1)
              )
            )
          )
        )

    val runner = Command.run(BSONSerializationPack)

    val s : Future[BSONDocument] = runner.apply(database, runner.rawCommand(queryBirthday)).one[BSONDocument]
    s.onComplete{
      case Success(result) =>
      result.get("result") match {
        case Some(array: BSONArray) =>
          array.get(0) match {
            case Some(doc: BSONDocument) =>
              val birthday = doc.getAs[Long]("birthday")
              // println(doc.getAs[Int]("count"))
              // println(birthday)
              //val lifetime = howManyToCompute(userID, birthday.getOrElse(DateTime.now.getMillis))              
            case e => 
              log.info(s"No result fot the user $userID: $e")
          }
        case e => 
          log.info(s"Error: $e is not a BSONArray")
      }
      case error =>
        log.info(s"The command has failed with error: $error")
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

    val s: Future[List[StatResponse]] = database[BSONCollection](DatabaseCollection.cacheCollection).
      find(query).
      sort(BSONDocument("computationTime" -> -1)). 
      cursor[StatResponse]().
      collect[List](1)

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
