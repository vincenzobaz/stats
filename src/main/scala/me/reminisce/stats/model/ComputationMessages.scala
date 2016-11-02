package me.reminisce.stats.model

import me.reminisce.stats.server.domain.RestMessage

object ComputationMessages {
  case class ComputeStatistics(userID: String) extends RestMessage
}
