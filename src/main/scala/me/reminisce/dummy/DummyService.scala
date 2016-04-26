package me.reminisce.dummy


import akka.actor._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.database.MongoDBEntities._
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}

import scala.concurrent.ExecutionContext.Implicits.global
import me.reminisce.server.GameEntities._

object DummyService {

 case class InsertEntity(entity: EntityMessage) extends RestMessage
 case class GetStatistics(userID: String) extends RestMessage
 case class ComputeStatistics(userID: String)
 case class Result(name: String, score: Int) //dummy
 case class Recompute(ids: List[String])


 def props(database: DefaultDB): Props = 
 Props(new DummyService(database))
}

class DummyService(database: DefaultDB) extends Actor with ActorLogging {

  import DummyService._

  def receive : Receive = waiting

// Waiting for new request
  def waiting : Receive = {
    case InsertEntity(entity) =>
      val dummyWorker = context.actorOf(DummyWorker.props(database))
      context.become(inserting)
      dummyWorker ! DummyService.InsertEntity(entity)
    case GetStatistics(userID) => 
      val worker = context.actorOf(DummyWorker.props(database))
      context.become(gettingStats)
      worker ! DummyService.GetStatistics(userID)   
    case ComputeStatistics(userID) =>
      val worker = context.actorOf(DummyWorker.props(database))
      context.become(computingStats(1))
      worker ! DummyService.ComputeStatistics(userID)   
    case _ => 
      log.info("Unexpected message has been received in waiting state")
  }

// Inserting a new entity in the db. No other request can be executed 
  def inserting : Receive = {
    case DummyWorker.Done => 
      log.info("Insertion accomplished")
      context.become(waiting)
    case DummyWorker.Abort => 
      log.info("Insertion aborted")
      context.become(waiting)
    case Recompute(ids) => 
      log.info(s"Recompute stats for $ids")
      ids.foreach{ id =>
        val worker = context.actorOf(DummyWorker.props(database))
        worker ! DummyService.ComputeStatistics(id)
        //TODO pool of workers ? When we have too many stats to compute
      } 
      context.become(computingStats(ids.length))   
    case _ => 
      log.info("Unexpected message has been received in inserting state")
      context.become(waiting)
  }

// Computing and inserting the new statistic after a new entities has been inserted. No other request can be executed
  def computingStats(nb: Int): Receive = {
    case DummyWorker.Done =>
      val running = nb - 1
      if (running == 0){
        log.info("Statistics computation and insertion accomplished")        
        context.become(waiting)
      } else {
        log.info(s"Remaining running computation: $running")
        context.become(computingStats(running))
      }

    case DummyWorker.Abort =>
      val running = nb -1
      if (running == 0){
        log.info("Statistics computation and insertion accomplished")        
        context.become(waiting)
      } else {
        log.info(s"Remaining running computation: $running")
        context.become(computingStats(running))
      }
    case _ =>
      log.info("Unexpected message has been received in computingStats state")
      context.become(waiting)
  }

  def gettingStats : Receive = {
    case DummyWorker.ResultStat(stats) =>
      log.info(s"Statistics for id: ${stats.userID} is $stats")
    case Result(name, score) => 
      println("result received")
      context.become(waiting)
    case _ =>
    log.info("Unexpected message has been received in gettingStats state")
    context.become(waiting)
  }
}
