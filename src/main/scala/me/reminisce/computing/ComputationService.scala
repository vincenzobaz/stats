package me.reminisce.computing


import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.model.ComputationMessages._
import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind
import me.reminisce.inserting.InsertionService._
import me.reminisce.model.InsertionMessages._

import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray}
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.github.nscala_time.time.Imports._
import org.joda.time.{DateTime, Days, Weeks, Months, Years}


object ComputationService {
  def props(database: DefaultDB): Props =
    Props(new ComputationService(database))
  }

class ComputationService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive: Receive = waitingRequest

  def waitingRequest: Receive = {

    case ComputeStatsWithTimeline(userID, timeline, allTime) =>
    println(s"timeline in CS")
      val daysManager = (IntervalKind.daily, context.actorOf(ComputationManager.props(database, IntervalKind.daily)))
      val weeksManager = (IntervalKind.weekly, context.actorOf(ComputationManager.props(database, IntervalKind.weekly)))
      val monthsManager = (IntervalKind.monthly, context.actorOf(ComputationManager.props(database, IntervalKind.monthly)))
      val yearsManager = (IntervalKind.yearly, context.actorOf(ComputationManager.props(database, IntervalKind.yearly)))
      val allTimeManager = (IntervalKind.allTime, context.actorOf(ComputationManager.props(database, IntervalKind.allTime)))
      
      val managersMaps: Map[IntervalKind, ActorRef] = 
        if(allTime)
          Map(daysManager, weeksManager, monthsManager, yearsManager, allTimeManager)
        else 
          Map(daysManager, weeksManager, monthsManager, yearsManager)

      println(s"managers :${managersMaps.size}")
      managersMaps.foreach { 
        //TODO use another message with allTime
        case (manager, ref) => ref ! ComputeStatsWithTimeline(userID, timeline, allTime)
      }
      context.become(waitingComputation(sender, userID, FrequencyOfPlays(), managersMaps.size))   
    
    // (in case of cache)
    case ComputeStatistics(userID) =>
      computeStatisticsForID(userID)
      // TODO
     
    case o => 
      log.info(s"Unexpected message $o received in waitingRequest state")
  }

  def waitingComputation(client: ActorRef, userID: String,acc: FrequencyOfPlays, remaining: Int): Receive = {
    case DailyStats(days) =>
      val FrequencyOfPlays(d, w, m, y, a) = acc
      val newAcc = FrequencyOfPlays(days, w, m, y, a)
      isComplete(userID, client, newAcc, remaining)
    case WeeklyStats(weeks) =>
      val FrequencyOfPlays(d, w, m, y, a) = acc
      val newAcc = FrequencyOfPlays(d, weeks, m, y, a)
      isComplete(userID, client, newAcc, remaining)
    case MonthlyStats(months) =>
      val FrequencyOfPlays(d, w, m, y, a) = acc
      val newAcc = FrequencyOfPlays(d, w, months, y, a)
      isComplete(userID, client, newAcc, remaining)
    case YearlyStats(years) =>
      val FrequencyOfPlays(d, w, m, y, a) = acc
      val newAcc = FrequencyOfPlays(d, w, m, years, a)
      isComplete(userID, client, newAcc, remaining)
    case AllTimeStats(all) =>
      val FrequencyOfPlays(d, w, m, y, a) = acc
      val newAcc = FrequencyOfPlays(d, w, m, y, Some(all))
      isComplete(userID, client, newAcc, remaining)
    case o => 
      log.info(s"Unexpected message $o received in waitingComputation state")
  }

  def isComplete(userID: String, client: ActorRef, acc: FrequencyOfPlays, remaining: Int): Unit = {
    val newRemaining = remaining -1
    if (newRemaining == 0){
        //TODO stats complete -> insert it on db 
      log.info(s"computation completed:  $acc" )
      client ! StatResponse(userID, acc)
      //insertStat(client, stat)
    } else {
      context.become(waitingComputation(client, userID, acc, newRemaining))
    }
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
      log.info(s"[ComputationService] Unexpected message  $o received in waitingStats state")
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
  /*  val service = context.actorOf(InsertionService.props(database))
    service ! InsertEntity(stat)
    context.become(waitingInsertion(client, service, 5, stat))
  */}

  private def computeStatisticsForID(userID: String){
    
    /*
    * 1. Get the "age" of the user's first play
    * 2. Compute how days, weeks, ... that have to be computed
    */
    import me.reminisce.model.DatabaseCollection
    val queryBirthday = 
      BSONDocument(
        "aggregate" -> DatabaseCollection.gameTestCollection,
        "pipeline" -> BSONArray(
            BSONDocument(
              "$match" -> BSONDocument(
                "status" -> "ended",
                s"{$userID}_Scores" -> BSONDocument(
                  "$exist" -> true
                ) 
              )
            ),
            BSONDocument(
              "$group" -> BSONDocument(
                "_id" -> s"${userID}_Score",
                "birthday" -> BSONDocument("$min" -> "$creationTime")
              )
            )
          )
        )

    val runner = Command.run(BSONSerializationPack)

    val s : Future[BSONDocument] = runner.apply(database, runner.rawCommand(queryBirthday)).one[BSONDocument]
    s.onComplete{
      case Success(result) =>
      result.get("result") match {
        case Some(array: BSONArray) =>
          array.get(0) match {
            case Some(doc: BSONDocument) =>
              val birthday = doc.getAs[Long]("birthday")
              val lifetime = howManyToCompute(userID, birthday.getOrElse(DateTime.now.getMillis))
              
              
            case e => 
              log.info(s"No result fot the user $userID")
          }
        case e => 
          log.info(s"Error: $e is not a BSONArray")
      }
      case error =>
        log.info(s"The command has failed with error: $error")
    }
  }
  private def howManyToCompute(userID: String, birthday: Long): Timeline = {
    val today = DateTime.now
    val firstPlay = birthday.toDateTime
    val days = Days.daysBetween(firstPlay, today).getDays
    val weeks = Weeks.weeksBetween(firstPlay, today).getWeeks
    val months = Months.monthsBetween(firstPlay, today).getMonths
    val years = Years.yearsBetween(firstPlay, today).getYears
    val ul = Timeline(userID, birthday, days, weeks, months, years)
    println(ul)
    ul
  }
}
