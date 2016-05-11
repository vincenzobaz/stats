package me.reminisce.model

import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.model.ComputationMessages.StatisticKind.StatisticKind

object ComputationMessages {
  case class ComputeStatistics(userID: String) extends RestMessage
  case class ResponseStatistics(message: String) extends RestMessage
  case object Done
  case object Abort
  case class Compute(kind: StatisticKind, userID: String)
  case class ComputedStatistic(stat: Statistic)

  object StatisticKind extends Enumeration {
    type StatisticKind = Value
    val AverageScore = Value("AverageScore")
    val CountCorrectQuestion = Value("CountCorrectQuestion")
    val CountWinnerGame = Value("CountWinnerGame")
  } 
}