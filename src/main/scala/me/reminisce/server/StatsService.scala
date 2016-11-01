package me.reminisce.server

import akka.actor._
import com.github.nscala_time.time.Imports._
import me.reminisce.inserting.InsertionService
import me.reminisce.model.InsertionMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.retrieving.RetrievingService
import me.reminisce.server.GameEntities._
import me.reminisce.server.domain.{RESTHandlerCreator, RestMessage}
import me.reminisce.server.jsonserializer.StatsFormatter
import reactivemongo.api.{DefaultDB, MongoConnection}
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object StatsService

/**
  * Defines a generic StatServiceActor
  */
trait StatsServiceActor extends StatsService {
  def actorRefFactory = context

  def receive = runRoute(statsRoutes)
}

object GameFormat extends Json4sSupport with StatsFormatter {}

/**
  * Defines a GameCreatorService with the handled routes.
  */
trait StatsService extends HttpService with RESTHandlerCreator with Actor with ActorLogging {
  val dbName: String
  val dbConnection: MongoConnection
  val statsRoutes = {

    import GameFormat._

    path("hello") {
      get {
        complete {
          <h1>Hi guys ! :)</h1>
        }
      }
    } ~ path("insertEntity") {
      post {
        extract(_.request.headers)
        entity(as[Game]) {
          game => {
            insertDB {
              InsertEntity(game)
            }
          }
        }
      }
    } ~ path("stats") {
      get {
        parameterSeq {
          params =>
            parseParameters(params) match {
              case Some(rs) =>
                log.info(s"Getting stats from ${rs.from} to ${rs.to} with limit ${rs.limit} for user ${rs.userID}.")
                retrieveStats(rs)
              case None =>
                log.error(s"Could not parse parameters.")
                complete {
                  "Unknown or malformed request"
                }
            }
        }
      }
    }
  }

  def actorRefFactory: ActorContext

  def parseParameters(params: Seq[(String, String)]): Option[RetrieveStats] = {
    lazy val formatter = DateTimeFormat.forPattern("dd-MM-yyyy")

    val (userId, from, to, limit) = params.foldLeft(("", List[DateTime](), List[DateTime](), 0)) {
      case ((id, f, t, l), (key, value)) =>
        key match {
          case "userId" if id.isEmpty => (id + value, f, t, l)
          case "from" =>
            try {
              val date = DateTime.parse(value, formatter)
              (id, f :+ date, t, l)
            }
            catch {
              case e: Throwable => (id, f, t, l)
            }
          case "to" =>
            try {
              val date = DateTime.parse(value, formatter)
              (id, f, t :+ date, l)
            }
            catch {
              case e: Throwable => (id, f, t, l)
            }
          case "limit" =>
            (id, f, t, value.toInt)
          case _ => (id, f, t, l)
        }
    }
    val optFrom = from.headOption
    val optTo = to.headOption
    val optLimit = if (limit != 0) Some(limit) else None

    Some(RetrieveStats(userId, optFrom, optTo, optLimit))
  }

  private def insertDB(message: RestMessage): Route = {
    handleWithDb {
      (db, ctx) =>
        val insertionService = context.actorOf(InsertionService.props(db))
        perRequest(ctx, insertionService, message)
    }
  }

  private def retrieveStats(message: RestMessage): Route = {
    handleWithDb {
      (db, ctx) =>
        val retrievingService = context.actorOf(RetrievingService.props(db))
        perRequest(ctx, retrievingService, message)
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
}
