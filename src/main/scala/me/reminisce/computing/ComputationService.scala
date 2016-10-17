package me.reminisce.computing

import akka.actor._
import me.reminisce.statistics.Stats._
import me.reminisce.server.GameEntities._
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.inserting.InsertionService
import me.reminisce.retrieving.RetrievingService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.model.Messages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.statistics.Utils._
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONArray, BSONString, BSONObjectID}
import reactivemongo.api.collections.bson._
import reactivemongo.api.commands.Command
import org.joda.time.DateTime
import scala.util.{Failure, Success}
import scala.concurrent.Future
import com.github.nscala_time.time.Imports._

object ComputationService {
  def props(database: DefaultDB): Props =
    Props(new ComputationService(database))
  }

class ComputationService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = waitingRequest
  val collectionGame = database[BSONCollection](DatabaseCollection.gameCollection)
  val collectionStats = database[BSONCollection](DatabaseCollection.statsCollection)

  def waitingRequest: Receive = {
    case ComputeStatistics(userId) =>
      compute(userId)
    case o => 
      log.info(s"Unexpected message $o received in computation service")
  }

  def compute( userId: String): Unit = {
        
    val userScore = s"${userId}_Scores"
    val queryGame = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                )
              )
    val g : Future[List[Game]] = collectionGame.find(queryGame).cursor[Game]().collect[List]()
    g.onComplete{
        case Success(games) =>
          val stats = aggregate(games, userId)
          insertOrUpdate(userId, stats)
        case Failure(e) =>
          log.error(s"Could not reach the database: $e")
          context.parent ! Abort
      }
    }    

    def insertOrUpdate(userId: String, stats: StatsEntities) : Unit = {
        
      val now: DateTime = DateTime.now
      val midnightToday = new DateTime(now.getYear, now.getMonthOfYear, now.getDayOfMonth, 0 , 0 , 0)
      log.error(s"heure : $midnightToday ${midnightToday.getMillis}")
      val queryStats = BSONDocument(
          "userId" -> userId
        )
      val s : Future[List[StatsEntities]] = collectionStats.find(queryStats).cursor[StatsEntities]().collect[List]()
      s.onComplete {
        case Success(existingStats) =>
          val todayStats = existingStats.filter(x => x.date > midnightToday)
          if(todayStats.isEmpty) {
            val future = collectionStats.insert(stats)
            future.onComplete {
              case Failure(e) => 
                context.parent ! Abort
              case Success(lastError) => 
                context.parent ! Done
            }
          } else {
            val selector = BSONDocument("_id" -> todayStats.head.id)
            println(s"stats id: ${stats.id}")
            val StatsEntities(id, userId, date, amount, win, lost, tie, rivals, questionsByType) = stats

            val modifier = BSONDocument(
                "$set" -> BSONDocument(
                "date" -> date,
                "amount" -> amount,
                "win" -> win,
                "lost" -> lost,
                "tie" -> tie,
                "rivals" -> rivals,
                "questionsByType" -> questionsByType))

            val futureUpdate = collectionStats.update(selector, modifier)
            futureUpdate.onComplete {
              case Success(lastError) =>
                context.parent ! Done
              case Failure(e) =>
                log.error("Update failure")
                context.parent ! Abort
            }
          }
        case Failure(e) =>
          log.error(s"Could not reach the database: $e")
          context.parent ! Abort
      }
    }

    def aggregate(games: List[Game], userId: String) : StatsEntities = {
      
      val (win, lost, tie, amount): (Int, Int, Int, Int) = games.foldLeft[(Int, Int, Int, Int)]((0, 0, 0, 0)){
        case ((w, l, t, a), Game(_, p1, p2, _, _, _, _, p1s, p2s, _, _, _, _, _)) =>        
          val (score, rival) = if (p1 == userId) (p1s, p2s) else (p2s, p1s)         
            if(score > rival) {
              (w + 1, l, t, a + 1)
            } else {
              if(score < rival){
                (w, l + 1, t, a + 1)
              } else {
                (w, l, t + 1, a + 1)
              }
            }
      }
      val rivals: Set[String] = games.foldLeft[Set[String]]((Set())){
        case (r, Game(_, p1, p2, _, _, _, _, _, _, _, _, _, _, _)) =>
          if (p1 == userId) r + p2 else r + p1
      }
      val questionsByType = emptyQuestionsByType()
      val id = BSONObjectID.generate
      println(s"Inserting stats $id")
      val stats = StatsEntities(id, userId, DateTime.now, amount, win, lost, tie, rivals, questionsByType)
      stats
    }

    def emptyStats(userId: String): StatsEntities = {
      StatsEntities(BSONObjectID.generate, userId, DateTime.now, 0, 0, 0, 0, Set(), emptyQuestionsByType())
    }
    def emptyQuestionsByType(): QuestionsByType = {
      val e = QuestionStats(0, 0, 0, 0, 0)
      QuestionsByType(e, e, e, e, e)
    }

  // def waitingRequest: Receive = {
    
  //   case ComputeStatistics(userID) =>

  //     val service = context.actorOf(RetrievingService.props(database))
  //     service ! GetFirstPlayDate(userID)
  //     context.become(waitingForRetrieving(sender, userID))
  //   case o => 
  //     log.info(s"Unexpected message $o received in waitingRequest state")
  // }

  // def waitingComputation(
  //     client: ActorRef, userID: String,acc: FrequencyOfPlays, remaining: Int): Receive = {
  //   val FrequencyOfPlays(d, w, m, y, a) = acc

  //   {
  //     case DailyStats(days) =>      
  //       val newAcc = FrequencyOfPlays(days, w, m, y, a)
  //       isComplete(userID, client, newAcc, remaining)
  //     case WeeklyStats(weeks) =>
  //       val newAcc = FrequencyOfPlays(d, weeks, m, y, a)
  //       isComplete(userID, client, newAcc, remaining)
  //     case MonthlyStats(months) =>
  //       val newAcc = FrequencyOfPlays(d, w, months, y, a)
  //       isComplete(userID, client, newAcc, remaining)
  //     case YearlyStats(years) =>
  //       val newAcc = FrequencyOfPlays(d, w, m, years, a)
  //       isComplete(userID, client, newAcc, remaining)
  //     case AllTimeStats(all) =>
  //       val newAcc = FrequencyOfPlays(d, w, m, y, Some(all))
  //       isComplete(userID, client, newAcc, remaining)
  //     case o => 
  //       log.info(s"Unexpected message $o received in waitingComputation state")
  //   }
  // }
  // /*
  //  * Wait for the data retrieving
  //  */
  // def waitingForRetrieving(client: ActorRef, userID: String): Receive = {
  //   case FirstPlayDate(date) =>
  //     sender ! PoisonPill
  //     val timeline = howManyToCompute(userID, date)
  //     splitRequestIntoManager(userID, timeline, true, client) 
  //   case msg @ UserNotFound(message) =>
  //     sender ! PoisonPill
  //     client ! Abort
  //   case o => 
  //     log.info(s"Unexpected message $o received in waitingForretrieving state")

  // }
  // /*
  //  * Collect all the statistics from the manager and send the StatResponse to the client
  //  */
  // def isComplete(userID: String, client: ActorRef, acc: FrequencyOfPlays, remaining: Int): Unit = {
  //   val newRemaining = remaining -1
  //   if (newRemaining == 0){
  //     val stats = StatResponse(userID, acc)
  //     client ! stats
  //     insertStat(client, stats)
  //   } else {
  //     context.become(waitingComputation(client, userID, acc, newRemaining))
  //   }
  // }

  // /*
  //  * Wait for the new computed Statistics entity insertion 
  //  */
  // def waitingInsertion(client: ActorRef, service: ActorRef, tryAgain: Int, stat: StatResponse): Receive = {
  //   case InsertionDone(message) => 
  //     service ! PoisonPill
  //     client ! Done
  //   case InsertionAbort(message) => 
  //     if(tryAgain != 0) {
  //       service ! InsertStatistic(stat)
  //       context.become(waitingInsertion(client, service, tryAgain - 1, stat))
  //     } else {
  //       client ! Abort
  //       service ! PoisonPill
  //     }
  // }

  // /*
  //  * Instantiate a Insertion Service and start the insertion of the statistics entity
  //  */
  // def insertStat(client: ActorRef, stat: StatResponse){
  //   val service = context.actorOf(InsertionService.props(database))
  //   service ! InsertStatistic(stat)
  //   context.become(waitingInsertion(client, service, 5, stat))    
  // }

  // /*
  //  * Dispatch the computation request to managers
  //  */
  // private def splitRequestIntoManager(userID: String, timeline: Timeline, allTime:Boolean, client: ActorRef) = {
  //   val daysManager = (IntervalKind.daily, context.actorOf(ComputationManager.props(database, IntervalKind.daily)))
  //   val weeksManager = (IntervalKind.weekly, context.actorOf(ComputationManager.props(database, IntervalKind.weekly)))
  //   val monthsManager = (IntervalKind.monthly, context.actorOf(ComputationManager.props(database, IntervalKind.monthly)))
  //   val yearsManager = (IntervalKind.yearly, context.actorOf(ComputationManager.props(database, IntervalKind.yearly)))
        
  //   val managersMaps: Map[IntervalKind, ActorRef] = 
  //     if(allTime) {
  //       val allTimeManager = (IntervalKind.allTime, context.actorOf(ComputationManager.props(database, IntervalKind.allTime)))
  //       Map(daysManager, weeksManager, monthsManager, yearsManager, allTimeManager)
  //     } else 
  //       Map(daysManager, weeksManager, monthsManager, yearsManager)

  //   managersMaps.foreach { 
  //     case (manager, ref) => ref ! ComputeStatsWithTimeline(userID, timeline, allTime)
  //   }
  //   context.become(waitingComputation(client, userID, FrequencyOfPlays(), managersMaps.size))   
  // }

  // /*
  //  * Compute the number of day, week, month and year since the first play of the user
  //  */
  // private def howManyToCompute(userID: String, firstPlay: DateTime): Timeline = {
  //   val today = DateTime.now
  //   val days = Days.daysBetween(firstPlay, today).getDays
  //   val weeks = Weeks.weeksBetween(firstPlay, today).getWeeks
  //   val months = Months.monthsBetween(firstPlay, today).getMonths
  //   val years = Years.yearsBetween(firstPlay, today).getYears
  //   val timeline = Timeline(userID, days, weeks, months, years)
  //   timeline
  // }
}
