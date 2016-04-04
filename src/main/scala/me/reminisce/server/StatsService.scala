package me.reminisce.server

import akka.actor._
import me.reminisce.server.domain.RESTHandlerCreator
import me.reminisce.dummy._
import me.reminisce.database._
import me.reminisce.database.MongoDBEntities._
import spray.routing._
import spray.httpx._
import spray.json._
import scala.concurrent.ExecutionContext.Implicits.global
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

object UserJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val PortoFolioFormats = jsonFormat3(User)
}

/**
  * Defines a GameCreatorService with the handled routes.
  */
trait StatsService extends HttpService with RESTHandlerCreator with Actor with ActorLogging {
  def actorRefFactory: ActorContext

  val db: DefaultDB
  
  val statsRoutes = {

    import UserJsonSupport._
   
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
    } ~ path("post"){ 
          post{           
            entity(as[User]) {user => {
              insertDB {
                DummyService.Insert(user)
              }              
            }
          }             
        }
    }
  }
    
  private def testDB(message: RestMessage): Route = {
    
    val dummyService = context.actorOf(DummyService.props(db))
    ctx => perRequest(ctx, dummyService, message)
  }  

  private def insertDB(message: RestMessage) : Route = {
  
    val dummyService = context.actorOf(DummyService.props(db))
    ctx => perRequest(ctx, dummyService, message)
  }
}

