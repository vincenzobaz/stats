package me.reminisce.retrieving

import akka.actor._
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.model.Messages._
import me.reminisce.statistics.StatisticEntities._
import reactivemongo.api.DefaultDB
import com.github.nscala_time.time.Imports._

object RetrievingService {
  def props(database: DefaultDB):Props =
    Props(new RetrievingService(database))
}

class RetrievingService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.RetrievingMessages._

  def receive: Receive = waitingForMessages
  /*
   * Initial State. Wait for a request
   */
  def waitingForMessages: Receive = {
    case msg @ RetrieveStats(userID, frequencies, allTime) if DatabaseCollection.UseCache =>
      val timeline = frequenciesToTimeline(userID, frequencies)
      val worker = context.actorOf(RetrievingWorker.props(database))
      worker ! RetrieveLastStatistics(userID)
      context.become(waitingForWorker(userID, sender, timeline, allTime))

    case msg @ RetrieveStats(userID, frequencies, allTime)  =>
      val timeline = frequenciesToTimeline(userID, frequencies)
      computeStatistics(userID, sender, timeline, allTime)
  
    case msg @ GetFirstPlayDate(userID) =>
      val worker = context.actorOf(RetrievingWorker.props(database))
      worker ! msg
      context.become(waitingForWorker(userID, sender))

    case o => log.info(s"Unexpected message ($o) received in RetrievingService")
  }

  /*
   * Wait for a response from a ComputationService.
   */
  def waitingForComputation(userID: String, client: ActorRef): Receive = {
    case Done =>
      sender ! PoisonPill
      context.self ! PoisonPill
    case Abort =>
      client ! StatisticsNotFound(s"Statistics not found for $userID")
      sender ! PoisonPill
      context.self ! PoisonPill
    case msg @ StatResponse(_, _, _) =>
      client ! StatisticsRetrieved(msg)
      // Doesn't stop the computation service yet, wait for insertion notification
    case o  => log.info(s"Unexpected message ($o) received in waitingForComputation state :(")
  }

  /*
   * Wait for a response from a RetrievingWorker
   */
  def waitingForWorker(userID: String, client: ActorRef, timeline: Timeline = null, allTime: Boolean = false): Receive = {
    case msg @ FirstPlayDate(date) =>
      client ! msg
      sender ! PoisonPill
      context.become(waitingForMessages)
    case StatisticsRetrieved(stats) =>
      sender ! PoisonPill
      if((stats.computationTime + 1.days) >= DateTime.now)
        client ! StatisticsRetrieved(stats)
      else
        computeStatistics(userID, client, timeline, allTime)
      
    case StatisticsNotFound(msg) =>
        computeStatistics(userID, client, timeline, allTime)
    case o  => log.info(s"[RS] Unexpected message ($o) received in waitingForWorker state")
  }

  /*
   * Instantiate a Computation Service and start the computation
   */
  private def computeStatistics(userID: String, client: ActorRef, timeline: Timeline, allTime: Boolean) = {
    val computationService = context.actorOf(ComputationService.props(database))
    computationService ! ComputeStatsWithTimeline(userID, timeline, allTime)
    context.become(waitingForComputation(userID, client))
  }

  /*
   * Return a Timeline class with the fields contained in frequencies or default values if missing.
   */
  private def frequenciesToTimeline(userID: String, frequencies: List[(String, Int)]): Timeline = 
  {
    val freqMap: Map[String, Int] = frequencies.toMap
    val day: Int = freqMap.getOrElse("day", 30)
    val week: Int = freqMap.getOrElse("week", 5)
    val month: Int = freqMap.getOrElse("month", 12)
    val year: Int = freqMap.getOrElse("year", 10)
    Timeline(userID, day, week, month, year)
  }
}
