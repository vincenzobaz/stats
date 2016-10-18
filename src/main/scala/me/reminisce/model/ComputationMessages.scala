package me.reminisce.model

import me.reminisce.server.domain.RestMessage

object ComputationMessages {
  case class ComputeStatistics(userID: String) extends RestMessage
}
