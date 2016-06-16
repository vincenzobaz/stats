package me.reminisce.inserting

import akka.actor._
import reactivemongo.api.DefaultDB
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.Messages._

object InsertionService {
  def props(database: DefaultDB):Props =
    Props(new InsertionService(database))
}

class InsertionService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive: Receive = waitingForMessages(null)

  def waitingForMessages(client: ActorRef): Receive = {
    case msg @ InsertEntity(game) => 
      val worker = context.actorOf(InsertionWorker.props(database))
      worker ! msg
      context.become(waitingForMessages(sender))
    case msg @ InsertStatistic(stats) =>
      val worker = context.actorOf(InsertionWorker.props(database))
      worker ! msg
      context.become(waitingForMessages(sender))
    case Inserted(ids) => 
      ids.foreach{ id =>
        client ! InsertionDone("Insertion completed")
        val worker = context.actorOf(ComputationService.props(database))
        worker ! ComputeStatistics(id)}
      sender ! PoisonPill 
    case Done => 
      client ! InsertionDone("Insertion completed")
      sender ! PoisonPill
    case Abort => 
      client ! InsertionAbort("Insertion aborted")
      sender ! PoisonPill
  }
}
