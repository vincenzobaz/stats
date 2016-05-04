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

    case class InsertEntity(entity: EntityMessage)
    case class ComputeStats(userID: String)
    case class InsertStats(stats: Statistic, collection: String)

  }

// TODO: use workers to compute each sub-stats -> send the result via message -> avoid to wait for the future


class MongoDatabaseService(db: DefaultDB) extends DatabaseService {

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

  def getAverageScore(userID: String) : Option[AverageScore] = {
    val s1 = computeCountsForAverageScore(userID, 1)
    //val s2 = computeCountsForAverageScore(userID, 2)
    //println(s"s1: $s1 s2: $s2")
 /*   if ( s1._1 != null && s2._1 != null) {
      AverageScore((s1._2 + s2._2) / s1._3 + s2._3)
    } else {
      AverageScore(2)
    }
    */
    s1 match{
      case Some(average) =>
        Some(AverageScore(average))
      case None =>
        None
    }
   // Some(AverageScore(avg))
    
  }
  def computeCountCorrectQuestion(userID: String): Option[CountCorrectQuestion] = {
    // TODO
    Some(CountCorrectQuestion(0,0))
  }

  def computeCountsForAverageScore(userID: String, player: Int): Option[Double] = {
    /*val p = if (player == 1) "player1" else "player2"
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
    )*/
    val idScores = s"${userID}_Scores"
    println(idScores)

    val unifiedQuery = BSONDocument(
      "aggregate" -> "toTest",
      "pipeline" -> BSONArray(
        BSONDocument("$match" -> BSONDocument(
          "$or" -> BSONArray(
            BSONDocument("player1" -> userID), 
            BSONDocument("player2" -> userID)
          ),
          "status" -> "ended")), 
        BSONDocument(
          "$group" -> BSONDocument(
            "_id" -> userID,
            "count" -> BSONDocument("$sum" -> 1),
            "toto" -> BSONDocument("$sum" -> idScores),
            "averageScore" -> BSONDocument("$avg" -> s"${userID}_Scores")
          )
        )
      )
    )

  //var counts: (String, Int, Int) = (null ,0, 0)
  var asd : Option[Double] = Some(0)
  val runner = Command.run(BSONSerializationPack)
  
  val s : Future[BSONDocument] = runner.apply(db, runner.rawCommand(unifiedQuery)).one[BSONDocument]
  s.onComplete{
    case Success(result) => 
      println(result)
      result.get("result") match { 
        case Some(array: BSONArray) =>
          println(array)
          array.get(0) match {
            case Some(doc: BSONDocument) =>
              println(doc)
              /*val id = doc.getAs[String]("_id").get
              val count = doc.getAs[Int]("count").get
              val sum = doc.getAs[Int]("sum").get 
              counts = (id, count, sum)*/
              val count = doc.getAs[Int]("count").get
              //val sum = doc.getAs[Double]("toto").get
              println(doc.getAs[Double]("averageScore"))
              //asd = Some(sum / count)
              //log.info(s"score average: $asd")
              //log.info(s"Stats computed for user $userID as player $player: $counts")
            case e => log.info(s"No results for the user $userID as player $player")
          }
        case e =>
          log.info(s"Error: $e is not a BSONArray")
    }    
    case error =>
      log.info(s"The command has failed with error: $error")
  }
    Await.result(s, 5000 millis)
    asd

  }
}
