package me.reminisce.stats.model

import me.reminisce.stats.server.domain.RestMessage
import me.reminisce.stats.statistics.Stats._
import com.github.nscala_time.time.Imports._
import scala.concurrent.Future

object RetrievingMessages {

  case class RetrieveStats(userID: String, from :Option[DateTime], to: Option[DateTime], limit: Option[Int]) extends RestMessage

  case class UserNotFound(message: String) extends RestMessage
  case class StatsRetrieved(stats: List[StatsEntities]) extends RestMessage
  } 
