package me.reminisce.server

import akka.actor._

import spray.routing._
import spray.httpx.Json4sSupport

import reactivemongo.api.DefaultDB

import me.reminisce.server.GameEntities._
import me.reminisce.server.jsonserializer.StatsFormatter
import me.reminisce.statsProcessing.StatsProcessingService
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}

object StatsService

/**
  * Defines a generic StatServiceActor
  */
trait StatsServiceActor extends StatsService {
  def actorRefFactory = context

  def receive = runRoute(statsRoutes)
}

object GameFormat extends Json4sSupport with StatsFormatter{}

/**
  * Defines a GameCreatorService with the handled routes.
  */
trait StatsService extends HttpService with RESTHandlerCreator with Actor with ActorLogging {
  def actorRefFactory: ActorContext

  val db: DefaultDB

  val statsRoutes = {
    
    import GameFormat._

    path("hello") {
      get {
        complete {
          <h1> Hi guys ! :) </h1>
        }

      }
    } ~ path("getStatistics"){
      get{
        parameters("userID"){
          (userID) =>
            testDB{
              StatsProcessingService.GetStatistics(userID)
            }
        }
      }   
    } ~ path("insertEntity"){
          post{
            entity(as[Game]) {
              game => {
                insertDB {
                  StatsProcessingService.InsertEntity(game)
               }
              } 
            }
          }
        }
  }
    
  private def testDB(message: RestMessage): Route = {
    
    val dummyService = context.actorOf(StatsProcessingService.props(db))
    ctx => perRequest(ctx, dummyService, message)
  }

  private def insertDB(message: RestMessage) : Route = {
  
    val dummyService = context.actorOf(StatsProcessingService.props(db))
    ctx => perRequest(ctx, dummyService, message)
  }
}

