package me.reminisce.statsProcessing

import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.server.domain.RestMessage
import reactivemongo.api.DefaultDB

object StatsProcessingService {

 case class InsertEntity(entity: EntityMessage) extends RestMessage
 case class GetStatistics(userID: String) extends RestMessage
 case class ComputeStatistics(userID: String) extends RestMessage
 case class Result(name: String, score: Int) //dummy
 case class Recompute(ids: List[String])
 case class ResponseMessage(message: String) extends RestMessage


 def props(database: DefaultDB): Props = 
 Props(new StatsProcessingService(database))
}

class StatsProcessingService(database: DefaultDB) extends Actor with ActorLogging {
   def receive : Receive = {
    case _ => ???
  }
  /*
  import StatsProcessingService._

  def receive : Receive = waiting

// Waiting for new request
  def waiting : Receive = {
    case InsertEntity(entity) =>
      val worker = context.actorOf(StatsProcessingWorker.props(database))
      context.become(inserting)
      worker ! InsertEntity(entity)
    case GetStatistics(userID) => 
      val client = sender
      val worker = context.actorOf(StatsProcessingWorker.props(database))
      context.become(gettingStats(sender))
      worker ! GetStatistics(userID)
    case ComputeStatistics(userID) =>
      val worker = context.actorOf(StatsProcessingWorker.props(database))
      context.become(computingStats(sender, 1))
      worker ! ComputeStatistics(userID)
    case _ => 
      log.info("Unexpected message has been received in waiting state")
  }

// Inserting a new entity in the db. No other request can be executed 
  def inserting : Receive = {
    case StatsProcessingWorker.Done =>
      log.info("Insertion accomplished")
      context.become(waiting)
    case StatsProcessingWorker.Abort =>
      log.info("Insertion aborted")
      context.become(waiting)
    case Recompute(ids) => 
      log.info(s"Recompute stats for $ids")
      ids.foreach{ id =>
        val worker = context.actorOf(StatsProcessingWorker.props(database))
        worker ! ComputeStatistics(id)
        //TODO pool of workers ? When we have too many stats to compute
      } 
      context.become(computingStats(null, ids.length))   
    case _ => 
      log.info("Unexpected message has been received in inserting state")
      context.become(waiting)
  }

// Computing and inserting the new statistic after a new entities has been inserted. No other request can be executed
  def computingStats(sender: ActorRef, nb: Int): Receive = {
    case StatsProcessingWorker.Done =>
      val running = nb - 1
      if (running == 0){
        log.info("Statistics computation and insertion accomplished")        
        context.become(waiting)    
        if(sender != null){    
          sender ! ResponseMessage("Stats well-computed")
        }
      } else {
        log.info(s"Remaining running computation: $running")
        context.become(computingStats(sender, running))
      }

    case StatsProcessingWorker.Abort =>
      val running = nb -1
      if (running == 0){
        log.info("Statistics computation and insertion accomplished")        
        context.become(waiting)  
        if (sender != null){      
          sender ! ResponseMessage("Stats computation aborted")
        }
      } else {
        log.info(s"Remaining running computation: $running")
        context.become(computingStats(sender, running))
      }
    case _ =>
      log.info("Unexpected message has been received in computingStats state")
      context.become(waiting)
  }

  def gettingStats(sender: ActorRef) : Receive = {
    case StatsProcessingWorker.ResultStat(stats) =>
      log.info(s"Statistics for id: ${stats.userID} is $stats")
      sender ! StatsProcessingWorker.ResultStat(stats)
      context.become(waiting)
    case Result(name, score) => 
      println("result received")
      context.become(waiting)
    case _ =>
    log.info("Unexpected message has been received in gettingStats state")
    context.become(waiting)
  }
  */
}
