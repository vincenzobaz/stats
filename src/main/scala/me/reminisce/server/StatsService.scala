package me.reminisce.server

import akka.actor._
import me.reminisce.server.domain.RESTHandlerCreator
import me.reminisce.dummy._
import me.reminisce.database._
import spray.routing._
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}

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

  val test = "coucou"
  val statsRoutes = {

   
    path("hello") {
      get {
        complete {
          <h1> Hi guys ! :) </h1>
        }
      }
    } ~ path("dbtest"){
      //parameters("") {
        //(UNUSED: String) =>
        log.info(s"In DB test path")
          testDB(DummyService.Search("Audrey Loeffel"))
     //}
      
    }

    
    

  }
  private def testDB(message: RestMessage): Route = {
    

    val dummyService = context.actorOf(Props[DummyService])
    log.info("DummyService created")
    ctx => perRequest(ctx, dummyService, message)
  }  
}
