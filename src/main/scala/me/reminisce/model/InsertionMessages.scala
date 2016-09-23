package me.reminisce.model

import me.reminisce.server.domain.RestMessage
import me.reminisce.server.GameEntities._
import me.reminisce.statistics.StatisticEntities._

object InsertionMessages {

  case class InsertEntity(game: Game) extends RestMessage
  case class InsertStatistic(stats: StatResponse)
  case class Inserted(ids: List[String])

  case class InsertionDone(status: String = "Done") extends RestMessage
  case class InsertionAbort(status: String = "Aborted") extends RestMessage
}