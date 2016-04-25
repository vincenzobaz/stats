package me.reminisce.dummy

import akka.actor._
import me.reminisce.database._
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError
import reactivemongo.bson._
import spray.json._
import spray.httpx._
import scala.concurrent.ExecutionContext.Implicits.global
import me.reminisce.server.GameEntities._
import me.reminisce.statistics.StatisticEntities._


object DummyWorker{

  case object Done
  case object Abort

  case class ResultStat(stat: Statistic)
  def props(database: DefaultDB): Props =
    Props(new DummyWorker(database))
}

class DummyWorker(database: DefaultDB) extends Actor with ActorLogging{
  import DummyWorker._
  
  val dbService = context.actorOf(MongoDatabaseService.props(database))

  def receive = {

    case DummyService.InsertEntity(entity) =>
      insertEntity(entity)

    case DummyService.GetStatistics(userID) =>
      log.info(s"Worker received a Search message with $userID.")
    case DummyService.ComputeStatistics(userID) =>
      log.info(s"Compute stats for $userID")
      dbService ! MongoDatabaseService.ComputeStats(userID)
    case ResultStat(stats: Stats) =>
      println(s"insert $stats in stats collection")
      //insertStatistic(stats)
      insertEntity(stats)
    case Done =>
      dbService ! PoisonPill // stop the DBService
      context.parent ! Done // Notify the service that the insertion is done
      log.info("PoisonPill sent to dbService, Done sent to parent.")
    case Abort =>
      dbService ! PoisonPill 
      context.parent ! Abort   
      log.info("PoisonPill sent to dbService, Abort sent to parent.")
  }



  def stop(): Unit = {
    log.info("Worker is stopped")
    context.stop(self)
  }

  def insertEntity(entity: EntityMessage): Unit = {
    
    entity match {
      case g: Game =>
        dbService ! MongoDatabaseService.InsertEntity(g)
      case s: Stats =>
      dbService ! MongoDatabaseService.InsertEntity(s)
      case _ =>
        log.info("unknown entity -- abort")
        context.parent ! Abort
    }    
  }
  /*def insertStatistic(stats: Statistic): Unit = {
    stats match {
      case s: Stats =>
      dbService ! MongoDatabaseService.InsertStats(s, "stats")
      case _ => ???
    }
  }
*/
}