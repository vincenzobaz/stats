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
 case class GetStatistics(userID: Int) extends RestMessage
 case class ComputeStatistics(userID: Int)
 case class Result(name: String, score: Int) //dummy


 def props(database: DefaultDB): Props = 
 Props(new DummyService(database))
}

class DummyService(database: DefaultDB) extends Actor with ActorLogging {

  import DummyService._

  def receive : Receive = waiting

// Waiting for new request
  def waiting : Receive = {
    case InsertEntity(entity) =>
      log.info(s"Entity Message $entity received by dummy service")
      val dummyWorker = context.actorOf(DummyWorker.props(database))
      dummyWorker ! DummyService.InsertEntity(entity)
      context.become(inserting)
    case GetStatistics(userID) => 
      println(userID)
      // TODO 
      context.become(gettingStats)
    case ComputeStatistics(userID) =>
      println(userID)
      // TODO
      context.become(computingStats)
    case _ => 
      log.info("Unexpected message has been received in waiting state")
  }

// inserting a new entity in the db. No other request can be executed 
  def inserting : Receive = {
    case DummyWorker.Done => 
      log.info("Insertion accomplished")
      context.become(waiting)
    case DummyWorker.Abort => 
      log.info("Insertion aborted")
      context.become(waiting)
    case _ => 
      log.info("Unexpected message has been received in inserting state")
      context.become(waiting)
  }

// computing the new statistic after a new entities has been inserted. No other request can be executed
  def computingStats : Receive = {
    case DummyWorker.ComputationDone =>
      log.info("Statistics computation accomplished")
      //TODO insert in DB
    case DummyWorker.InsertionDone => 
      log.info("Statistics insertion accomplished")
      context.become(waiting)
    case DummyWorker.Abort =>
      log.info("Computation aborted")
      context.become(waiting)
    case _ =>
      log.info("Unexpected message has been received in computingStats state")
      context.become(waiting)
  }

  def gettingStats : Receive = {
    case Result(name, score) => println("result received")
    case _ =>
    log.info("Unexpected message has been received in gettingStats state")
    context.become(waiting)

  }


}
