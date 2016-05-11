package me.reminisce.inserting

import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.server.domain.RestMessage
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object InsertionWorker {
  def props(database: DefaultDB):Props =
    Props(new InsertionWorker(database))
  
  val cacheCollection = "cachedStats"
  val gameCollection = "games"
 }

class InsertionWorker(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive : Receive = {
    case InsertEntity(entity: Entity) => insertInDb(entity)
    case o =>
      log.info(s"Unexpected message ($o) received in InsertionWorker")

  }

  def insertInDb(entity: Entity){
    entity match {
      case g: Game =>
        log.info(s"Inserting game ${g.player1} vs ${g.player2} in DB")
       // val col = db[BSONCollection](gameCollection)   
       val col = database[BSONCollection]("toTest") 
        val future = col.insert(g)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Game with lastError = " + lastError)

            val toRecompute = List(g.player1, g.player2)
            context.parent ! Inserted(toRecompute)
          }
        }
      case s: Statistic =>
        log.info(s"Inserting stats in DB")
        //val col = db[BSONCollection](cacheCollection)
        val col = database[BSONCollection]("toTestStats")
        val future = col.insert(s)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Stats with lastError = " + lastError)
            context.parent ! Done
          }
        }
    }
  }


}