package me.reminisce.dummy

import akka.actor._
import com.github.nscala_time.time.Imports._
import me.reminisce.database._
import me.reminisce.database.MongoDBEntities._
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError
import reactivemongo.bson._
import spray.json._
import spray.httpx._
import scala.concurrent.ExecutionContext.Implicits.global
import me.reminisce.server.GameEntities._


object DummyWorker{

  case object Done
  case object Abort

  def props(database: DefaultDB): Props =
    Props(new DummyWorker(database))
}

class DummyWorker(database: DefaultDB) extends Actor with ActorLogging{
  import DummyWorker._
  
  def receive = {

    case DummyService.InsertEntity(entity) =>
      insertEntity(entity)

    case DummyService.Search(username) =>

      log.info(s"Worker received a Search message with $username.")
      dummyQuery(username)
    
    case DummyService.Result(username, scores) =>
      context.parent ! DummyService.Result(username, scores)
    
    /* Used for dummy case with User
    case DummyService.Insert(message) =>
      dummyInsert(message)
      */

    case Done =>
      stop()
      //TODO kill the dbservice
  }

  def stop(): Unit = {
    log.info("Worker is stopped")
    context.stop(self)
  }

  def insertEntity(entity: EntityMessage): Unit = {
    
    entity match {
      case g: Game =>
        //val bson = GametoBson(g) 
        //val dbService = context.actorOf(MongoDatabaseService.props(database))
        //dbService ! MongoDatabaseService.InsertEntity(bson)
      case _ =>
        log.info("unknown entity -- abort")
        context.parent ! Abort
    }    
  }

  def dummyQuery(username: String): Unit = {
    
    val dbService = context.actorOf(MongoDatabaseService.props(database))
    dbService ! MongoDatabaseService.Query(username)
  }

  def dummyInsert(message: Message): Unit = {
    val dbService = context.actorOf(MongoDatabaseService.props(database))

    message match {
      case u: User =>
        val bson : BSONDocument = UserWriter.write(u)
        dbService ! MongoDatabaseService.Insert(bson)
      case _ => 
        log.info("Unknown Message model -- Killing dbService actor")
        dbService ! PoisonPill
    }
    
  }
}