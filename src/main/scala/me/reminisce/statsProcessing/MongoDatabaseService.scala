package me.reminisce.statsProcessing

import akka.actor.Props

import me.reminisce.server.GameEntities._
import me.reminisce.database._
import me.reminisce.statistics.StatisticEntities._

import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray}
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

import com.github.nscala_time.time.Imports._



/**
  * Factory for [[me.reminisce.database.MongoDatabaseService]], collection names definition, case class for message
  * passing and data conversion methods
  */
  object MongoDatabaseService {
  /**
    * Collection names definitions
    */
    //val usersCollection = "users"
   // val scoresCollection = "scores"
    val cacheCollection = "cachedStats"
    val gameCollection = "games"


  /**
    * MongoDB LastError object to be used while inserting in order to have safer insertion
    */
  
  /**
    * Creates a database service actor
    * @param db database into which data is inserted
    * @return props for the created MongoDatabaseService
    */
    def props(db: DefaultDB): Props =
    Props(new MongoDatabaseService(db))
/*
    case class InsertEntity(entity: EntityMessage)
    case class ComputeStats(userID: String)
    case class InsertStats(stats: Statistic, collection: String)
*/
  }

// TODO: use workers to compute each sub-stats -> send the result via message -> avoid to wait for the future


class MongoDatabaseService(db: DefaultDB) extends DatabaseService {
 def receive : Receive = {
    case _ => ???
  }
/*
  import MongoDatabaseService._
  import me.reminisce.server.GameEntities._
  import me.reminisce.statistics.StatisticEntities._


  def receive = {
    case InsertEntity(entity) =>
      insertInDb(entity)
    case InsertStats(stats, collection) =>
    case ComputeStats(userID) =>
      val countWinnerGame = computeCountWinnerGame(userID)
      val avgScore = getAverageScore(userID)
      val countCorrectQuestion = computeCountCorrectQuestion(userID)     
      val stats = Stats(userID, countWinnerGame, avgScore, countCorrectQuestion)
      println(stats)
      context.parent ! StatsProcessingWorker.InsertStat(stats)
    case StatsProcessingService.GetStatistics(userID) =>
      retrieveStats(userID)
  }

  def insertInDb(entity: EntityMessage){
    entity match {
      case g: Game =>
       // val col = db[BSONCollection](MongoDatabaseService.gameCollection)   
       val col = db[BSONCollection]("toTest") 
        val future = col.insert(g)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Game with lastError = " + lastError)
            val toRecompute = List(g.player1, g.player2)

            context.parent ! StatsProcessingService.Recompute(toRecompute)
            log.info(s"Recompute sent to dummy worker with $toRecompute")
          }
        }
      case s: Statistic =>
        //val col = db[BSONCollection](MongoDatabaseService.cacheCollection)
        val col = db[BSONCollection]("toTestStats")
        val future = col.insert(s)

        future.onComplete {
          case Failure(e) => throw e
          case Success(lastError) => {
            log.info("successfully inserted Stats with lastError = " + lastError)
            context.parent ! StatsProcessingWorker.Done
          }
        }
    }
  }

  def retrieveStats(userID: String): Unit = {
      
    val query = BSONDocument(
      "userID" -> userID
      )
    // TODO sort by Date and take the lastest

    val s: Future[List[Stats]] = db[BSONCollection](MongoDatabaseService.cacheCollection).
      find(query).
      cursor[Stats]().
      collect[List](1)

      s.onComplete{
        case Success(stats)  =>
          if(!stats.isEmpty) 
            context.parent ! StatsProcessingWorker.ResultStat(stats.head)
          else {
            val emptyStat = Stats(userID, None, None, None)
            context.parent !StatsProcessingWorker.ResultStat(emptyStat)
          }
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          context.parent ! StatsProcessingWorker.Abort
      }
  }

  def computeCountWinnerGame(userID: String): Option[CountWinnerGame] = {
    // TODO
  Some(CountWinnerGame(0, 0))
  }

  def computeCountCorrectQuestion(userID: String): Option[CountCorrectQuestion] = {
    // TODO
    Some(CountCorrectQuestion(0,0))
  }

  def getAverageScore(userID: String): Option[AverageScore] = {
    
    // The name's field depends on the userID
    val idScores = s"$$${userID}_Scores"
    val idScores2 = "$" + userID + "_Scores"

    println(idScores)
  
    val unifiedQuery = BSONDocument(
      "aggregate" -> "toTest",
      "pipeline" -> BSONArray(
        BSONDocument(
          "$match" -> BSONDocument(
            "status" -> "ended")), 
        BSONDocument(
          "$group" -> BSONDocument(
            "_id" -> userID,
            "count" -> BSONDocument("$sum" -> 1),
            "averageScore" -> BSONDocument("$avg" -> idScores)
          )
        )
      )
    )

    var average : Option[Double] = None

    val runner = Command.run(BSONSerializationPack)

    val s : Future[BSONDocument] = runner.apply(db, runner.rawCommand(unifiedQuery)).one[BSONDocument]
    s.onComplete{
      case Success(result) => 
        result.get("result") match { 
          case Some(array: BSONArray) =>
            array.get(0) match {
              case Some(doc: BSONDocument) =>
                average = doc.getAs[Double]("averageScore")
                println(average)
              case e => log.info(s"No results for the user $userID")
            }
          case e =>
            log.info(s"Error: $e is not a BSONArray")
      }    
      case error =>
        log.info(s"The command has failed with error: $error")
    }    

    Await.result(s, scala.concurrent.duration.Duration(5000, MILLISECONDS)) //ambiguity with nscalatime
    
    average match{
      case Some(a) =>
        log.info(s"$userID's average: $a")
        Some(AverageScore(a))
      case None =>
        None
    }
 }*/ 
}

