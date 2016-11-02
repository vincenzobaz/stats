package me.reminisce.stats.model

import me.reminisce.stats.server.domain.RestMessage
import me.reminisce.stats.server.GameEntities._

object InsertionMessages {

  case class InsertEntity(game: Game) extends RestMessage
  case class Inserted(ids: List[String])

  case class InsertionDone(status: String = "Done") extends RestMessage
  case class InsertionAbort(status: String = "Aborted") extends RestMessage
}