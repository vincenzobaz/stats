package me.reminisce.server

import akka.actor._
import me.reminisce.server.domain.RESTHandlerCreator
import spray.routing._

object StatsService

/**
  * Defines a generic GameCreatorServiceActor
  */
trait StatsServiceActor extends StatsService {
  def actorRefFactory = context

  def receive = runRoute(statsRoutes)
}

/**
  * Defines a GameCreatorService with the handled routes.
  */
trait StatsService extends HttpService with RESTHandlerCreator with Actor with ActorLogging {
  def actorRefFactory: ActorContext

  val statsRoutes = {

    //FIXME: This should display hello world
    path("hello") {
      get {
        complete {
          ???
        }
      }
    }

  }
}
