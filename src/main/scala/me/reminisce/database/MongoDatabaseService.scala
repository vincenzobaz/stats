package me.reminisce.database

import akka.actor.Props

import me.reminisce.server.GameEntities._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.server.ApplicationConfiguration
import me.reminisce.dummy._
import me.reminisce.statistics.StatisticEntities._

import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.core.commands.GetLastError
import reactivemongo.core.commands._
import reactivemongo.api._
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scala.util.{Failure, Success}


/**
  * Factory for [[me.reminisce.database.MongoDatabaseService]], collection names definition, case class for message
  * passing and data conversion methods
  */
  object MongoDatabaseService {
  /**
    * Collection names definitions
    */
    val usersCollection = "users"
    val scoresCollection = "scores"
    val cacheCollection = "cachedStats"
    val gameCollection = "games"


  /**
    * MongoDB LastError object to be used while inserting in order to have safer insertion
    */
  
  /**
    * Creates a database service actor
    * @param userId userId of the user fdor which the data is stored
    * @param db database into which data is inserted
    * @return props for the created MongoDatabaseService
    */
    def props(db: DefaultDB): Props =
    Props(new MongoDatabaseService(db))

    case class InsertEntity(entity: EntityMessage)
    case class ComputeStats(userID: String)
    case class InsertStats(stats: Statistic, collection: String)

  }

class MongoDatabaseService(db: DefaultDB) extends DatabaseService {

  import MongoDatabaseService._
  import me.reminisce.server.GameEntities._
  import me.reminisce.statistics.StatisticEntities._


  def receive = {
    case InsertEntity(entity) =>
      insertInDb(entity)
    case InsertStats(stats, collection) =>
    case ComputeStats(userID) =>
      val gameResume = getGameResume(userID)
      val avgScore = getAverageScore(userID)
      val questionResume = getQuestionResume(userID)
   
      val stats = Stats(userID, gameResume, avgScore, questionResume)
      context.parent ! DummyWorker.InsertStat(stats)
    case DummyService.GetStatistics(userID) => 
      getStats(userID)
  }

  def insertInDb(entity: EntityMessage){
    entity match {
      case g: Game =>
        val col = db[BSONCollection](MongoDatabaseService.gameCollection)   
        val future = col.insert(g)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Game with lastError = " + lastError)
            val toRecompute = List(g.player1, g.player2)

            context.parent ! DummyService.Recompute(toRecompute)
            log.info(s"Recompute sent to dummy worker with $toRecompute")
          }
        }
      case s: Statistic =>
        val col = db[BSONCollection](MongoDatabaseService.cacheCollection)   
        val future = col.insert(s)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Stats with lastError = " + lastError)
            context.parent ! DummyWorker.Done
          }
        }
    }
  }

  def getStats(userID: String): Unit = {
    val query = BSONDocument(
      "userID" -> userID
      )
    val s: Future[List[Stats]] = db[BSONCollection](MongoDatabaseService.cacheCollection).
      find(query).
      cursor[Stats].
      collect[List](1)
      println(s" stat: $s")

      s.onComplete{
        case Success(stats) =>
          context.parent ! DummyWorker.ResultStat(stats.head)
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          context.parent ! DummyWorker.Abort
      }
  }

  def getGameResume(userID: String): GameResume = {  
    // TODO
  GameResume(0, 0)
  }

  def getAverageScore(userID: String) : AverageScore = {
    val s1 = commandScoreResume(userID, 1)
    val s2 = commandScoreResume(userID, 2)
    println(s"s1: $s1 s2: $s2")
    if ( s1._1 != null && s2._1 != null) {
      AverageScore((s1._2 + s2._2) / s1._3 + s2._3)
    }
    AverageScore(2)
  }
  def getQuestionResume(userID: String): QuestionResume = {
    // TODO
    QuestionResume(0,0)
  }

  def commandScoreResume(userID: String, player: Int): (String, Int, Int) = {
    
    val p = if (player == 1) "player1" else "player2"
    val playerScores = if (player == 1) "$player1Scores" else "$player2Scores"
    
    val query = BSONDocument(
      "aggregate" -> MongoDatabaseService.gameCollection,
      "pipeline" -> BSONArray(
        BSONDocument("$match" -> BSONDocument(
          p -> userID,
          "status" -> "ended")
        ),
        BSONDocument(
          "$group" -> BSONDocument(
            "_id" -> ("$" + p),
            "count" -> BSONDocument("$sum" -> 1),
            "sum" -> BSONDocument("$sum" -> playerScores)
          )
        )
      )
    )

    var resume: (String, Int, Int) = (null ,0, 0)

    val score: Future[BSONDocument] = db.command(RawCommand(query))
    score.onComplete {
      case Success(result) => {
        result.get("result") match {
          case Some(array: BSONArray) =>
            array.get(0) match {
              case Some(doc: BSONDocument) =>
                  val id = doc.getAs[String]("_id").get
                  val count = doc.getAs[Int]("count").get
                  val sum = doc.getAs[Int]("sum").get
                  resume = (id, count, sum)
              case _ =>
              log.info("Empty result")
            }
          case _ =>
            log.info("Unknown result form")
        }
      }
      case o => {
        log.info("The query failed")
        context.parent ! DummyWorker.Abort
      } 
    }
    Await.result(score, 5000 millis)
    resume
  }
}
