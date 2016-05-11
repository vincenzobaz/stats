package me.reminisce.retrieving

import akka.actor._
import me.reminisce.server.domain.RestMessage
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.statistics.StatisticEntities._

import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object RetrievingWorker {
  def props(database: DefaultDB):Props =
    Props(new RetrievingWorker(database))
}

class RetrievingWorker(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = {
    case GetStatistics(userID) => 
      val client = sender
      log.info(s"retrieving stat")
      retrieveStats(userID, client)
    case o => log.info(s"Unexpected message ($o) received in RetrievingWorker")
  }

  def retrieveStats(userID: String, client: ActorRef): Unit = {
    import me.reminisce.model.DatabaseCollection
    println(s"self: $self, client: $client")
    val query = BSONDocument(
      "userID" -> userID
      )
    // TODO sort by Date and take the lastest

    val s: Future[List[Stats]] = database[BSONCollection](DatabaseCollection.cacheTestCollection).
      find(query).
      cursor[Stats]().
      collect[List](1)

      s.onComplete{
        case Success(stats)  =>
          if(!stats.isEmpty) {
            log.info("stats !")
            client ! StatisticsRetrieved(stats.head)
          } else {
            log.info("no stats !")
            val emptyStat = Stats(userID, None, None, None)
            client ! StatisticsRetrieved(emptyStat)
          }
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          client ! Abort
      }
  }

}