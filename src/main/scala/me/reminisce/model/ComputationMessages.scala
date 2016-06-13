package me.reminisce.model

import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.model.ComputationMessages.SubStatisticKind.SubStatisticKind
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind
import com.github.nscala_time.time.Imports._

object ComputationMessages {
  case class ComputeStatistics(userID: String) extends RestMessage
  case class ResponseStatistics(message: String) extends RestMessage
  case object Done
  case object AbortComputation
  //case class Compute(kind: StatisticKind, userID: String)
  case class ComputedStatistic(stat: Statistic)
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
    case class CorrectStat(nb: Int)
    case class PercentCorrectStat(pourcent: Double)
    case class QuestionBreakDownStat(qbd: List[QuestionsBreakDown])
    case class GamesPlayedAgainstStat(gpa: List[GamesPlayedAgainst])
    case class ResponseStatOnInterval(stat: StatsOnInterval)

  /*object StatisticKind extends EnumeraCtion {
    type StatisticKind = Value
    val AverageScore = Value("AverageScore")
    val CountCorrectQuestion = Value("CountCorrectQuestion")
    val CountWinnerGame = Value("CountWinnerGame")
  } */
  object SubStatisticKind extends Enumeration {
    type SubStatisticKind = Value
    val amount = Value("Amount")
    val correct = Value("Correct")
    val percentCorrect = Value("PercentCorrect")
    val questionBreakDown = Value("QuestionBreakDown")
    val gamePlayedAgainst = Value("GamePlayedAgainst")
  } 

  case class Timeline(userID: String, firstPlay: Long, day: Int = 30, week: Int = 5, month: Int = 12, year: Int = 10)

}