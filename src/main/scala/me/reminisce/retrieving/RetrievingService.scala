package me.reminisce.retrieving

import akka.actor._
import me.reminisce.server.domain.RestMessage
import reactivemongo.api.DefaultDB
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._

object RetrievingService {
  def props(database: DefaultDB):Props =
    Props(new RetrievingService(database))
}

class RetrievingService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = waitingForMessages(null) 

  def waitingForMessages(client: ActorRef): Receive = {
    case msg @ GetStatistics(userID) =>
      val worker = context.actorOf(RetrievingWorker.props(database))
      worker ! msg
      context.become(waitingForMessages(sender))
    case msg @ StatisticsRetrieved(stat) =>
      client ! msg
    case Abort => 
      client ! StatisticsNotFound("Statistics not found")
    case o => log.info(s"Unexpected message ($o) received in RetrievingService")
 
  }

}
