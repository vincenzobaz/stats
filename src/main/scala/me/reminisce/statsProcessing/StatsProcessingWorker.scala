package me.reminisce.statsProcessing

import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.server.domain.RestMessage

import reactivemongo.api.DefaultDB

object StatsProcessingWorker{

  case object Done
  case object Abort
  case class ResultStat(stat: Stats) extends RestMessage
  case class InsertStat(stat: Statistic)

  def props(database: DefaultDB): Props =
    Props(new StatsProcessingWorker(database))
}

class StatsProcessingWorker(database: DefaultDB) extends Actor with ActorLogging{
  import StatsProcessingWorker._
  def receive : Receive = {
    case _ => ???
  }
  /*
  val dbService = context.actorOf(MongoDatabaseService.props(database))

  def receive = {

    case StatsProcessingService.InsertEntity(entity) =>
      insertEntity(entity)
    case StatsProcessingService.GetStatistics(userID) =>
      dbService ! StatsProcessingService.GetStatistics(userID)
    case StatsProcessingService.ComputeStatistics(userID) =>
      dbService ! MongoDatabaseService.ComputeStats(userID)
    case InsertStat(stats: Stats) =>
      insertEntity(stats)
    case ResultStat(stats: Stats) =>
      context.parent ! ResultStat(stats)
    case StatsProcessingService.Recompute(ids) =>
      context.parent ! StatsProcessingService.Recompute(ids)
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
  */
}