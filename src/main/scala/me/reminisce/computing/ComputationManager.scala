package me.reminisce.computing


import akka.actor._
import me.reminisce.model.ComputationMessages._
import reactivemongo.api.DefaultDB

import me.reminisce.statistics.StatisticEntities._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind


import com.github.nscala_time.time.Imports._

object ComputationManager {
  def props(database: DefaultDB, kind: IntervalKind): Props =
    Props(new ComputationManager(database, kind))
  }

class ComputationManager(database: DefaultDB, kind: IntervalKind) extends Actor with ActorLogging {
  val today = DateTime.now
  def receive: Receive = waitingForRequest 

  def waitingForRequest: Receive = {
    case ComputeStatsWithTimeline(userID, timeline, allTime) => 
      val maxValue = getAgoValue(timeline)
      (0 to maxValue).foreach{ ago => 
        val worker = context.actorOf(ComputationWorker.props(database, kind, ago))
        val interval = getInterval(ago)
        worker ! ComputeStatsInInterval(userID, interval._1, interval._2)
      }
      context.become(WaitingForResponses(sender, maxValue + 1))
    case m => log.info(s"[CM $kind] Unexpected message $m received in waitingForRequest")
  }

  def WaitingForResponses(client: ActorRef, remaining: Int, stats: List[StatsOnInterval] = List()): Receive = {
    case ResponseStatOnInterval(s: StatsOnInterval) => 
      val newRemaining = remaining -1
      if (newRemaining == 0) {
        val sortedStats = (s+: stats).sortBy{
          case StatsOnInterval(ago, _, _, _, _, _) => ago
        }
        kind match {
          case IntervalKind.daily => 
            client ! DailyStats(sortedStats)
          case IntervalKind.weekly => 
            client ! WeeklyStats(sortedStats)
          case IntervalKind.monthly => 
            client ! MonthlyStats(sortedStats)
          case IntervalKind.yearly => 
            client ! YearlyStats(sortedStats)
          case IntervalKind.allTime =>
            client ! AllTimeStats(sortedStats)
        }
        context.become(waitingForRequest)
      } else {
        context.become(WaitingForResponses(client, newRemaining, s +: stats))
      }

    case AbortComputation => 
      client ! AbortComputation
      // or should we add an empty statsonInterval ?
      // the client will kill this service and all others workers
  }
  
  private def getInterval(ago: Int): (DateTime, DateTime) = {
    val duration = kind match {
      case IntervalKind.daily => 1.days
      case IntervalKind.weekly => 1.weeks
      case IntervalKind.monthly => 1.months
      case IntervalKind.yearly => 1.year
      case IntervalKind.allTime => 10.year // TODO get the "age" of the user
    }
    val from = today - (ago * duration) - duration
    val to = today - (ago * duration)
    (from, to)
  }

  private def getAgoValue(timeline: Timeline): Int = {
    kind match {
      case IntervalKind.daily => timeline.day
      case IntervalKind.weekly => timeline.week
      case IntervalKind.monthly => timeline.month
      case IntervalKind.yearly => timeline.year
      case IntervalKind.allTime => 0
    }
  }
}
