package me.reminisce.dummy


import akka.actor._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.database.MongoDBEntities._
import reactivemongo.api.DefaultDB
//import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}

import scala.concurrent.ExecutionContext.Implicits.global

object DummyService {

 case class Search(username: String) extends RestMessage
 case class Result(username: String, score: Double)
 case class Insert(msg: Message) extends RestMessage


 def props(database: DefaultDB): Props = 
 Props(new DummyService(database))
}

class DummyService(database: DefaultDB) extends Actor with ActorLogging {

  import DummyService._

  def receive = {
  	case Search(username) =>

   val dummyWorker = context.actorOf(DummyWorker.props(database))    
   dummyWorker ! DummyService.Search(username)

   case Result(username, score) =>
   log.info(s" $username's score average is: $score")

   case Insert(message) =>
   log.info("message receive by dummy service")
   val dummyWorker = context.actorOf(DummyWorker.props(database))    
   dummyWorker ! DummyService.Insert(message)
 }
}
