package me.reminisce.computing

import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind
import me.reminisce.inserting.InsertionService
import me.reminisce.retrieving.RetrievingService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.model.Messages._

import reactivemongo.api.DefaultDB
import org.joda.time.{DateTime, Days, Weeks, Months, Years}


object ComputationService {
  def props(database: DefaultDB): Props =
    Props(new ComputationService(database))
  }

class ComputationService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive: Receive = waitingRequest

  def waitingRequest: Receive = {
    
    case ComputeStatistics(userID) =>

      val service = context.actorOf(RetrievingService.props(database))
      service ! GetFirstPlayDate(userID)
      context.become(waitingForRetrieving(sender, userID))
           
    case o => 
      log.info(s"Unexpected message $o received in waitingRequest state")
  }

  def waitingComputation(
      client: ActorRef, userID: String,acc: FrequencyOfPlays, remaining: Int): Receive = {
    val FrequencyOfPlays(d, w, m, y, a) = acc

    {
      case DailyStats(days) =>      
        val newAcc = FrequencyOfPlays(days, w, m, y, a)
        isComplete(userID, client, newAcc, remaining)
      case WeeklyStats(weeks) =>
        val newAcc = FrequencyOfPlays(d, weeks, m, y, a)
        isComplete(userID, client, newAcc, remaining)
      case MonthlyStats(months) =>
        val newAcc = FrequencyOfPlays(d, w, months, y, a)
        isComplete(userID, client, newAcc, remaining)
      case YearlyStats(years) =>
        val newAcc = FrequencyOfPlays(d, w, m, years, a)
        isComplete(userID, client, newAcc, remaining)
      case AllTimeStats(all) =>
        val newAcc = FrequencyOfPlays(d, w, m, y, Some(all))
        isComplete(userID, client, newAcc, remaining)
      case o => 
        log.info(s"Unexpected message $o received in waitingComputation state")
    }
  }
  /*
   * Wait for the data retrieving
   */
  def waitingForRetrieving(client: ActorRef, userID: String): Receive = {
    case FirstPlayDate(date) =>
      sender ! PoisonPill
      val timeline = howManyToCompute(userID, date)
      splitRequestIntoManager(userID, timeline, true, client) 
    case msg @ UserNotFound(message) =>
      sender ! PoisonPill
      client ! Abort
    case o => 
      log.info(s"Unexpected message $o received in waitingForretrieving state")

  }
  /*
   * Collect all the statistics from the manager and send the StatResponse to the client
   */
  def isComplete(userID: String, client: ActorRef, acc: FrequencyOfPlays, remaining: Int): Unit = {
    val newRemaining = remaining -1
    if (newRemaining == 0){
      val stats = StatResponse(userID, acc)
      client ! stats
      insertStat(client, stats)
    } else {
      context.become(waitingComputation(client, userID, acc, newRemaining))
    }
  }

  /*
   * Wait for the new computed Statistics entity insertion 
   */
  def waitingInsertion(client: ActorRef, service: ActorRef, tryAgain: Int, stat: StatResponse): Receive = {
    case InsertionDone(message) => 
      service ! PoisonPill
      client ! Done
    case InsertionAbort(message) => 
      if(tryAgain != 0) {
        service ! InsertStatistic(stat)
        context.become(waitingInsertion(client, service, tryAgain - 1, stat))
      } else {
        client ! Abort
        service ! PoisonPill
      }
  }

  /*
   * Instantiate a Insertion Service and start the insertion of the statistics entity
   */
  def insertStat(client: ActorRef, stat: StatResponse){
    val service = context.actorOf(InsertionService.props(database))
    service ! InsertStatistic(stat)
    context.become(waitingInsertion(client, service, 5, stat))    
  }

  /*
   * Dispatch the computation request to managers
   */
  private def splitRequestIntoManager(userID: String, timeline: Timeline, allTime:Boolean, client: ActorRef) = {
    val daysManager = (IntervalKind.daily, context.actorOf(ComputationManager.props(database, IntervalKind.daily)))
    val weeksManager = (IntervalKind.weekly, context.actorOf(ComputationManager.props(database, IntervalKind.weekly)))
    val monthsManager = (IntervalKind.monthly, context.actorOf(ComputationManager.props(database, IntervalKind.monthly)))
    val yearsManager = (IntervalKind.yearly, context.actorOf(ComputationManager.props(database, IntervalKind.yearly)))
        
    val managersMaps: Map[IntervalKind, ActorRef] = 
      if(allTime) {
        val allTimeManager = (IntervalKind.allTime, context.actorOf(ComputationManager.props(database, IntervalKind.allTime)))
        Map(daysManager, weeksManager, monthsManager, yearsManager, allTimeManager)
      } else 
        Map(daysManager, weeksManager, monthsManager, yearsManager)

    managersMaps.foreach { 
      //TODO use another message with allTime
      case (manager, ref) => ref ! ComputeStatsWithTimeline(userID, timeline, allTime)
    }
    context.become(waitingComputation(client, userID, FrequencyOfPlays(), managersMaps.size))   
  }

  /*
   * Compute the number of day, week, month and year since the first play of the user
   */
  private def howManyToCompute(userID: String, firstPlay: DateTime): Timeline = {
    val today = DateTime.now
    val days = Days.daysBetween(firstPlay, today).getDays
    val weeks = Weeks.weeksBetween(firstPlay, today).getWeeks
    val months = Months.monthsBetween(firstPlay, today).getMonths
    val years = Years.yearsBetween(firstPlay, today).getYears
    val timeline = Timeline(userID, days, weeks, months, years)
    println(timeline)
    timeline
  }
}
