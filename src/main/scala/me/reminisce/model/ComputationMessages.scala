package me.reminisce.model

import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.model.ComputationMessages.SubStatisticKind.SubStatisticKind
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind
import com.github.nscala_time.time.Imports._

object ComputationMessages {
  case class ComputeStatistics(userID: String) extends RestMessage
  case class ResponseStatistics(message: String) extends RestMessage
  case object AbortComputation
  
//--- Service
  case class ComputeStatsInInterval(userID: String, from: DateTime, to: DateTime)
//--- Manager
  case class ComputeStatsWithTimeline(userID: String, timeline: Timeline, allTime: Boolean)
  case class DailyStats(days: List[StatsOnInterval])
  case class WeeklyStats(weeks: List[StatsOnInterval])
  case class MonthlyStats(months: List[StatsOnInterval])
  case class YearlyStats(years: List[StatsOnInterval])
  case class AllTimeStats(allTime: List[StatsOnInterval])
//--- Worker
  case class ComputeSubStat(userID: String, kind: SubStatisticKind, from: DateTime, to: DateTime)
  case class AmountStat(a: Int)
  case class WonStat(nb: Int)
  case class LostStat(nb: Int)
  case class QuestionBreakDownStat(qbd: List[QuestionsBreakDown])
  case class GamesPlayedAgainstStat(gpa: List[GamesPlayedAgainst])
  case class ResponseStatOnInterval(stat: StatsOnInterval)

  object SubStatisticKind extends Enumeration {
    type SubStatisticKind = Value
    val amount = Value("Amount")
    val won = Value("Won")
    val lost = Value("lost")
    val questionBreakDown = Value("QuestionBreakDown")
    val gamePlayedAgainst = Value("GamePlayedAgainst")
  } 
    case class Timeline(userID: String, day: Int, week: Int, month: Int, year: Int)
}
