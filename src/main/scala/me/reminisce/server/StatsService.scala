package me.reminisce.server

import akka.actor._

import spray.routing._
import spray.httpx.Json4sSupport

import reactivemongo.api.DefaultDB

import me.reminisce.server.GameEntities._
import me.reminisce.server.jsonserializer.StatsFormatter
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}
import me.reminisce.model.InsertionMessages._
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.inserting.InsertionService
import me.reminisce.computing.ComputationService
import me.reminisce.retrieving.RetrievingService

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
              GetStatistics(userID)
            }
        }
      }   
    } ~ path("insertEntity"){
          post{
            entity(as[Game]) {
              game => {
                insertDB {
                  InsertEntity(game)
                }
              } 
            }
          }
        } ~ path("computeStatistics"){
          get{
            parameters("userID"){
              (userID) =>
                computeStat{
                  ComputeStatistics(userID)
            }
        }
      }   
    }
  }
    
  private def testDB(message: RestMessage): Route = {
    
    val retrievingService = context.actorOf(RetrievingService.props(db))
    ctx => perRequest(ctx, retrievingService, message)
  }

  private def insertDB(message: RestMessage) : Route = {
  
    val insertionService = context.actorOf(InsertionService.props(db))
    ctx => perRequest(ctx, insertionService, message)
  }
  private def computeStat(message: RestMessage) : Route = {

    val computationService = context.actorOf(ComputationService.props(db))
    ctx => perRequest(ctx, computationService, message)
  }
}
