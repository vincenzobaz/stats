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
  lazy val possibleFrequency: Set[String] = Set("day", "week", "month", "year")

  val statsRoutes = {
    
    import GameFormat._

    path("hello"){
      get {
        complete {
          <h1> Hi guys ! :) </h1>
        }
      }
    } ~ path("insertEntity"){
          post{
            extract(_.request.headers).map(println)
            entity(as[Game]) {
              game => {
                insertDB {
                  InsertEntity(game)
                }
              } 
            }
          }
    } ~ path("stats"){
        get{
          parameterSeq {
            params =>        
              parseParameters(params) match {
                case Some(rs)=>
                  retrieveStats(rs)
                case None =>
                  complete{
                    "Unknown or malformed request"
                  }
              }           
          }
        }
      }   
  }
    
  private def insertDB(message: RestMessage) : Route = {
    val insertionService = context.actorOf(InsertionService.props(db))
    ctx => perRequest(ctx, insertionService, message)
  }
  /*private def computeStat(message: RestMessage) : Route = {

    val computationService = context.actorOf(ComputationService.props(db))
    ctx => perRequest(ctx, computationService, message)
  }*/
  private def retrieveStats(message: RestMessage) : Route = {
    val retrievingService = context.actorOf(RetrievingService.props(db))
    ctx => perRequest(ctx, retrievingService, message)
  }

  def parseParameters(params: Seq[(String, String)]) : Option[RetrieveStats] = {    
    lazy val IsNumeric = """^(\d+)$""".r
    val (userID, frequency, allTime, error) = params.foldLeft(("", List[(String, Int)](), 0, 0)){
      case (acc, (key, value)) => 
        key match {
          case "userId" if acc._1.isEmpty => (acc._1 + value, acc._2, acc._3, acc._4)
          case "frequency" =>
            value.split(":") match {
              case Array(k: String, IsNumeric(i)) if isValidFrequency(k, acc._2) => 
                (acc._1, acc._2 :+ (k, i.toInt), acc._3, acc._4)
              case _ => 
                (acc._1, acc._2, acc._3, acc._4 + 1) 
            }             
          case "allTime" => (acc._1, acc._2, acc._3 + 1, acc._4)
          case _ => (acc._1, acc._2, acc._3, acc._4 + 1)
        }      
    }

    lazy val a = allTime != 0
    error match {
      case 0 => Some(RetrieveStats(userID, frequency, a))
      case _ => None
    }
  }

  private def isValidFrequency(f: String, frequencies : List[(String, Int)]) : Boolean = {
    possibleFrequency(f) && frequencies.forall{case (a, _) => a != f}
  }
}
