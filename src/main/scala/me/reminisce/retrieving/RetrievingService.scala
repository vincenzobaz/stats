package me.reminisce.retrieving

import akka.actor._
import me.reminisce.server.domain.RestMessage
import me.reminisce.computing.ComputationService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.model.Messages._
import me.reminisce.statistics.StatisticEntities._
import reactivemongo.api.DefaultDB

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
    case msg @ RetrieveStats(userID, frequencies, allTime) if DatabaseCollection.UseCache=>
      // TODO
      // 1. get the lastest stats entity
      // 2. Compare with today date
      // 3. if same day -> return it else compute

      sender ! StatisticsRetrieved(StatResponse(userID, FrequencyOfPlays()))


    case msg @ RetrieveStats(userID, frequencies, allTime)  =>
      val timeline = frequenciesToTimeline(userID, frequencies)
      val computationService = context.actorOf(ComputationService.props(database))
      computationService ! ComputeStatsWithTimeline(userID, timeline, allTime)
      context.become(waitingForComputation(sender))
  
    case msg @ GetFirstPlayDate(userID) =>
      val worker = context.actorOf(RetrievingWorker.props(database))
      worker ! msg
      context.become(waitingForWorker(sender))

    case o => log.info(s"Unexpected message ($o) received in RetrievingService")
  }
  /*
   * Wait for a response from a ComputationService.
   */
  def waitingForComputation(client: ActorRef): Receive = {
    case Done =>
      log.info(s"Stats have been inserted")
      sender ! PoisonPill
      context.self ! PoisonPill
    case msg @ StatResponse(_, _, _) =>
      client ! StatisticsRetrieved(msg)
      // Doesn't stop the computation service yet, wait for insertion notification
    case Abort =>
      log.info(s"Abort: Stats haven't been inserted")
      sender ! PoisonPill
      context.self ! PoisonPill
    case o  => log.info(s"Unexpected message ($o) received in waitingForComputation state :(")
  }
  /*
   * Wait for a response from a RetrievingWorker
   */
  def waitingForWorker(client: ActorRef): Receive = {
    case msg @ FirstPlayDate(date) =>
      client ! msg
      sender ! PoisonPill
      context.become(waitingForMessages)
    case o  => log.info(s"Unexpected message ($o) received in waitingForAge state")

  }
  /*
   * Return a Timeline class with the fields contained in frequencies or default values if missing.
   */
  private def frequenciesToTimeline(userID: String, frequencies: List[(String, Int)]): Timeline = {
    val freqMap: Map[String, Int] = frequencies.toMap
    val day: Int = freqMap.getOrElse("day", 30)
    val week: Int = freqMap.getOrElse("week", 5)
    val month: Int = freqMap.getOrElse("month", 12)
    val year: Int = freqMap.getOrElse("year", 10)
    Timeline(userID, day, week, month, year)
  }

}
