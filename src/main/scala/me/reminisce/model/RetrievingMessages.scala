package me.reminisce.model

import me.reminisce.server.domain.RestMessage
import me.reminisce.statistics.StatisticEntities._

object RetrievingMessages {
  case object Abort
  case class GetStatistics(userID: String) extends RestMessage
  case class StatisticsRetrieved(stat: StatResponse) extends RestMessage
  case class StatisticsNotFound(message: String) extends RestMessage
  case class RetrieveStats(userID: String, frequency: List[(String, Int)], allTime: Boolean) extends RestMessage

  }
