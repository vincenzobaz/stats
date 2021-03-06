package me.reminisce.server

import akka.actor._
import spray.routing._
import spray.httpx.Json4sSupport
import reactivemongo.api.{DefaultDB, MongoConnection}
import me.reminisce.server.GameEntities._
import me.reminisce.server.jsonserializer.StatsFormatter
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}
import me.reminisce.model.InsertionMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.inserting.InsertionService
import me.reminisce.retrieving.RetrievingService
import com.github.nscala_time.time.Imports._
import spray.http.StatusCodes._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

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

  val dbName: String
  val dbConnection: MongoConnection
 
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
            extract(_.request.headers)
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

  private def handleWithDb(handler: (DefaultDB, RequestContext) => Unit): Route = {
    ctx =>
      dbConnection.database(dbName).onComplete {
        case Success(db) =>
          handler(db, ctx)
        case Failure(e) =>
          complete(InternalServerError, s"${e.getMessage}")
      }
  }
    
  private def insertDB(message: RestMessage) : Route = {
    handleWithDb {
      (db, ctx) =>
        val insertionService = context.actorOf(InsertionService.props(db))
        perRequest(ctx, insertionService, message)
    }
  }
  
  private def retrieveStats(message: RestMessage) : Route = {
    handleWithDb {
      (db, ctx) =>
        val retrievingService = context.actorOf(RetrievingService.props(db))
        perRequest(ctx, retrievingService, message)
    }
  }

  def parseParameters(params: Seq[(String, String)]) : Option[RetrieveStats] = {
    lazy val formatter = DateTimeFormat.forPattern("dd-MM-yyyy")

    val (userId, from, to) = params.foldLeft(("", List[DateTime](), List[DateTime]())){
      case ((id, f, t), (key, value)) =>
        key match {
          case "userId" if id.isEmpty => (id+value, f, t)
          case "from" => 
            try{
              val date = DateTime.parse(value, formatter)
              (id, f :+ date, t)
            }
            catch{
              case e : Throwable => (id, f, t)
            }
          case "to" => 
            try{
              val date = DateTime.parse(value, formatter)
              (id, f, t :+ date)
            }
            catch{
              case e : Throwable => (id, f, t)
            }
          case _ => (id, f, t)
        }
    }
    val optFrom = from.headOption
    val optTo = to.headOption
    Some(RetrieveStats(userId, optFrom, optTo))
  }
}
