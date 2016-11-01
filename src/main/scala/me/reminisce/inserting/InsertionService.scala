package me.reminisce.inserting

import akka.actor._
import reactivemongo.api.DefaultDB
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.Messages._
import me.reminisce.model.InsertionMessages._

object InsertionService {
  def props(database: DefaultDB):Props =
    Props(new InsertionService(database))
}

class InsertionService(database: DefaultDB) extends Actor with ActorLogging {

  def receive: Receive = waitingForMessages(null)

  def waitingForMessages(client: ActorRef): Receive = {
    case msg @ InsertEntity(game) => 
      log.info(s"Received game: $game")
      val worker = context.actorOf(InsertionWorker.props(database))
      worker ! msg
      context.become(waitingForMessages(sender))
    case Inserted(ids) => 
      ids.foreach{ id =>
        client ! InsertionDone()
        val worker = context.actorOf(ComputationService.props(database))
        worker ! ComputeStatistics(id)}
      sender ! PoisonPill 
    case Done => 
      client ! InsertionDone()
      sender ! PoisonPill
    case Abort => 
      client ! InsertionAbort()
      sender ! PoisonPill
  }
}
