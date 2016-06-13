package me.reminisce.retrieving

import akka.actor._
import me.reminisce.server.domain.RestMessage
import reactivemongo.api.DefaultDB
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.statistics.StatisticEntities._


object RetrievingService {
  def props(database: DefaultDB):Props =
    Props(new RetrievingService(database))
}

class RetrievingService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = waitingForMessages

  def waitingForMessages: Receive = {
    case msg @ RetrieveStats(userID, frequencies, allTime) if DatabaseCollection.UseCache=>
      // TODO
      sender ! StatisticsRetrieved(StatResponse(userID, FrequencyOfPlays()))
    case msg @ RetrieveStats(userID, frequencies, allTime)  =>
      log.info("No cache, need computation")
      val timeline = frequenciesToTimeline(userID, frequencies)
      val computationService = context.actorOf(ComputationService.props(database))
      computationService ! ComputeStatsWithTimeline(userID, timeline, allTime)
      context.become(waitingForComputation(sender))
    
      //TODO in a different state
    /*case msg @ GetStatistics(userID) =>
      val worker = context.actorOf(RetrievingWorker.props(database))
      worker ! msg
      context.become(waitingForMessages)
    // case msg @ StatisticsRetrieved(stat) =>
    //   client ! msg
    case Abort => 
      client ! StatisticsNotFound("Statistics not found")
*/    
    case o => log.info(s"Unexpected message ($o) received in RetrievingService")
  }
  
  def waitingForComputation(client: ActorRef): Receive = {
    case msg @ StatResponse(userID, frequencies) =>
      client ! msg
      sender ! PoisonPill
  }

  private def frequenciesToTimeline(userID: String, frequencies: List[(String, Int)]): Timeline = {
    val freqMap: Map[String, Int] = frequencies.toMap
    val day: Int = freqMap.getOrElse("day",30)
    val month: Int = freqMap.getOrElse("month", 5)
    val week: Int = freqMap.getOrElse("week", 12)
    val year: Int = freqMap.getOrElse("year", 10)
    Timeline(userID, day, month, week, year)
  }

}
