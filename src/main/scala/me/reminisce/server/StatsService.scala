package me.reminisce.server

import akka.actor._
import me.reminisce.server.domain.RESTHandlerCreator
import me.reminisce.dummy._
import me.reminisce.database._
import spray.routing._
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}
import reactivemongo.api.DefaultDB

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

  val db: DefaultDB
  
  val statsRoutes = {

   
    path("hello") {
      get {
        complete {
          <h1> Hi guys ! :) </h1>
        }
      }
    } ~ path("dbtest"){
      get{
        parameters("username"){
          (username) =>
           testDB{
            DummyService.Search(username)
           }
        }
      }
   
    }
  }


  private def testDB(message: RestMessage): Route = {
    
    val dummyService = context.actorOf(DummyService.props(db))
    ctx => perRequest(ctx, dummyService, message)
  }  
}
