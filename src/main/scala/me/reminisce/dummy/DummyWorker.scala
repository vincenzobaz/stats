package me.reminisce.dummy

import akka.actor._
import com.github.nscala_time.time.Imports._
import me.reminisce.database.MongoDatabaseService._
import reactivemongo.api.DefaultDB
//import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError

import scala.concurrent.ExecutionContext.Implicits.global

object DummyWorker{

  case object Done
  case object Abort

  def props(/*database: DefaultDB*/): Props = 
    Props(new DummyWorker(/*database*/))
}

class DummyWorker() extends Actor with ActorLogging{
	import DummyWorker._
	def receive = {
	  case DummyService.Search(username) =>
	  	log.info(s"Worker received a Search message with $username.")
	    context.parent ! DummyService.Result(username, 123634)
	  case Done =>
	    stop()
	}

	def stop(): Unit = {
		log.info("Worker is stopped")
		context.stop(self)
	}
}