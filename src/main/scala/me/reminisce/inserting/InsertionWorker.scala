package me.reminisce.inserting

import akka.actor._
import me.reminisce.server.GameEntities._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.model.DatabaseCollection
import me.reminisce.model.Messages._
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
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
    case InsertEntity(entity: Game) => insertInDb(entity)
    case InsertStatistic(stats: StatResponse) => insertStatInDB(stats)
    case o =>
      log.info(s"Unexpected message ($o) received in InsertionWorker")
  }

  /*
   * Insert a Game in the database and send the id's of players to the client
   */
  def insertInDb(entity: Game) = {  
    val col = database[BSONCollection](DatabaseCollection.gameCollection)
    val future = col.insert(entity)

    future.onComplete {
      case Failure(e) => context.parent ! Abort
      case Success(lastError) => {
        val toRecompute = List(entity.player1, entity.player2)
        context.parent ! Inserted(toRecompute)
      }
    }
  }

  /*
   * Insert a Statistics entity in the database and send tha status insertion to the client
   */
  def insertStatInDB(stats: StatResponse) {
    
     // TODO WHY the one in StatisticEntites is not imported ???
    import reactivemongo.bson._
    implicit val StatWriter: BSONDocumentWriter[StatResponse] = Macros.writer[StatResponse]
    
    val col = database[BSONCollection](DatabaseCollection.cacheCollection)
    val future = col.insert(stats)

    future.onComplete {
      case Failure(e) => 
        context.parent ! Abort
      case Success(lastError) => {
        context.parent ! Done
      }
    }
  }
}
