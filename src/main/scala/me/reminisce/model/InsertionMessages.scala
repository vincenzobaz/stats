package me.reminisce.model

import me.reminisce.server.domain.RestMessage

object InsertionMessages {

  trait Entity

  case class InsertEntity(entity: Entity) extends RestMessage
  case class Inserted(ids: List[String])
  case object Done
  case object Abort
  case class InsertionDone(message: String) extends RestMessage
  case class InsertionAbort(message: String) extends RestMessage
}