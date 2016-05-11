package me.reminisce.computing


import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.model.ComputationMessages._
import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.inserting.InsertionService  
import me.reminisce.model.InsertionMessages._
import reactivemongo.api.DefaultDB

object ComputationService {
  def props(database: DefaultDB): Props =
    Props(new ComputationService(database))
  }

class ComputationService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive: Receive = waitingRequest(null)

  def waitingRequest(client: ActorRef): Receive = {
    case ComputeStatistics(userID) =>
      val client = sender
      context.become(waitingStats(client, Stats(userID, None, None, None), StatisticKind.values.size))
      log.info(s"Compute statistics for user $userID")
      StatisticKind.values.foreach{
        value =>
        val worker = context.actorOf(ComputationWorker.props(database))
        worker ! Compute(value, userID) // why the service receives these messages ??
      }
     
    case o => 
      log.info(s"unexpected message $o received in waitingRequest state")
  }
  def waitingInsertion(client: ActorRef, service: ActorRef, tryAgain: Int, stat: Stats): Receive = {
    case InsertionDone(message) => 
      service ! PoisonPill
      client ! Done
    case abort @ InsertionAbort(message) => 
      if(tryAgain != 0) {
        service ! InsertEntity(stat)
        context.become(waitingInsertion(client, service, tryAgain - 1, stat))
      } else {
        client ! abort
        service ! PoisonPill
      }
  }

  def waitingStats(client: ActorRef, stat: Stats, missingStats: Int) : Receive = {
    case avgs @ AverageScore(average) =>
      val Stats(userID, cwg, _, ccq, date, id) = stat
      val newStat = Stats(userID, cwg, Some(avgs), ccq, date, id)
      isComplete(client, newStat, missingStats)
    case ccq @ CountCorrectQuestion(correct, wrong) => 
      val Stats(userID, cwg, avg, _, date, id) = stat
      val newStat = Stats(userID, cwg, avg, Some(ccq), date, id)
      isComplete(client, newStat, missingStats)
    case cwg @ CountWinnerGame(won, lost) => 
      val Stats(userID, _, avg, ccq, date, id) = stat
      val newStat = Stats(userID, Some(cwg), avg, ccq, date, id)
      isComplete(client, newStat, missingStats)
    case Abort => log.info("An error occured when computing statistics")
    case o => 
      log.info(s"unexpected message  $o received in waitingStats state")
  }
  def isComplete(client: ActorRef, stat: Stats, missingStats: Int): Unit = {
    val newMissingStats = missingStats -1
    if (newMissingStats == 0){
        //TODO stats complete -> insert it on db 
        log.info(s"computation completed:  $stat" )
        insertStat(client, stat)
      } else {
        log.info(s"Remaining computation: $newMissingStats")
        context.become(waitingStats(client, stat, newMissingStats))
      }
  }

  def insertStat(client: ActorRef, stat: Stats){
    val service = context.actorOf(InsertionService.props(database))
    service ! InsertEntity(stat)
    context.become(waitingInsertion(client, service, 5, stat))
  }
}