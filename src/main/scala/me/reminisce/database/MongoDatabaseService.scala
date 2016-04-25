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
    val statsCollection = "games"


  /**
    * MongoDB LastError object to be used while inserting in order to have safer insertion
    */
  //val safeLastError = new GetLastError(w = Some(BSONInteger(1)))

  /**
    * Creates a database service actor
    * @param userId userId of the user fdor which the data is stored
    * @param db database into which data is inserted
    * @return props for the created MongoDatabaseService
    */
    def props(db: DefaultDB): Props =
    Props(new MongoDatabaseService(db))

    case class InsertEntity(entity: EntityMessage, collection: String)
    case class ComputeStats(userID: String)

  }

  class MongoDatabaseService(db: DefaultDB) extends DatabaseService {

    import MongoDatabaseService._
    import me.reminisce.server.GameEntities._
    import me.reminisce.statistics.StatisticEntities._


    def receive = {
      case InsertEntity(entity, collection) =>
        insertInDb(entity, collection)
      case ComputeStats(userID) =>
        val gameResume = getGameResume(userID)
        val avgScore = getAverageScore(userID)
        val totalQuestion = getTotalQuestion(userID)
     
        val stats = Stats(userID, gameResume, avgScore, totalQuestion)
        context.parent ! DummyWorker.ResultStat(stats)
    }

    def insertInDb(entity: EntityMessage, collection: String){
      entity match {
        case g: Game =>
          val col = db[BSONCollection](collection)   
          val future = col.insert(g)

          future.onComplete {
            case Failure(e) => throw e
            case Success(lastError) => {
              log.info("successfully inserted document with lastError = " + lastError)
              context.parent ! DummyWorker.Done
            }
          }
      }
    }

    def getGameResume(userID: String): GameResume = {
      
/*
      val query = BSONDocument(
        "aggregate" -> MongoDatabaseService.statsCollection,
        "pipeline" -> BSONArray(
          BSONDocument("$match" -> BSONDocument(
            "player1" -> userID,
            "status" -> "ended")),
          BSONDocument(
            "$group" -> BSONDocument(
              "_id" -> "$player1",
              "count" -> BSONDocument("$sum" ->1),
              "sum" -> BSONDocument("$sum" -> "$player1Scores")
            )
          )
        )
      )

      var result = GameResume(0,0)

      val player1: Future[BSONDocument] = db.command(RawCommand(query))
      player1.onComplete {
        case Success(result) => {
          log.info("Success !")
          result.get("result") match {
            case Some(array: BSONArray) =>
              array.get(0) match {
                case Some(doc: BSONDocument) =>

                    val id = doc.getAs[String]("_id").get
                    val count = doc.getAs[Int]("count").get
                    val sum = doc.getAs[Int]("sum").get
                    println(s"won game $id $count $sum")
                    result = GameResume(co)
              }
            case _ =>
              println("unknow format")
          }
          /*val array : BSONArray = result.get("result").get
          val score : BSONDocument = array.get(0).get
          val id = score.get("_id")
          val count = score.get("count")
          val sum = score.get("sum")
          println(s"$id $count $sum")*/
        }
        case o => {
          log.info("The query failed!")
          println(o)
          //context.parent ! DummyWorker.Abort
        } 
      }

    //  val queryPlayer2 = ???
    */
    GameResume(0, 1)

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
    def getTotalQuestion(userID: String): QuestionResume = {
      QuestionResume(3,2)
    }

    def commandScoreResume(userID: String, player: Int): (String, Int, Int) = {
      
      val p = if (player == 1) "player1" else "player2"
      val playerScores = if (player == 1) "$player1Scores" else "$player2Scores"
      
      val query = BSONDocument(
        "aggregate" -> MongoDatabaseService.statsCollection,
        "pipeline" -> BSONArray(
          BSONDocument("$match" -> BSONDocument(
            p -> userID,
            "status" -> "ended")),
          BSONDocument(
            "$group" -> BSONDocument(
              "_id" -> ("$" + p),
              "count" -> BSONDocument("$sum" -> 1),
              "sum" -> BSONDocument("$sum" -> playerScores)
            )
          )
        )
      )

      var resume: (String, Int, Int) = (null ,0,0)

      val score: Future[BSONDocument] = db.command(RawCommand(query))
      score.onComplete {
        case Success(result) => {
          log.info("Success !")
          result.get("result") match {
            case Some(array: BSONArray) =>
              array.get(0) match {
                case Some(doc: BSONDocument) =>

                    val id = doc.getAs[String]("_id").get
                    val count = doc.getAs[Int]("count").get
                    val sum = doc.getAs[Int]("sum").get
                    println(s"Game resume: $id $count $sum")
                    resume = (id, count, sum)
                case _ =>
                println("unexpected value.")
              }
            case _ =>
              println("unknow format")
          }
        }
        case o => {
          log.info("The query failed!")
          println(o)
          //context.parent ! DummyWorker.Abort
        } 
      }
      Await.result(score, 5000 millis)
      resume
    }

    /*
    def avgQuery(username: String){
      val usersCol = db.collection[BSONCollection](MongoDatabaseService.usersCollection)
      val scoreCol = db.collection[BSONCollection](MongoDatabaseService.scoresCollection)

      val query = BSONDocument(
        "$users.name" -> username)

      val emptyquery = BSONDocument()

      val averageScorePerPers = BSONDocument(
        "aggregate" -> "scores",
        "pipeline" -> BSONArray(
          BSONDocument(
            "$group" -> BSONDocument(
              "_id" -> "$users.name",
              "avgScore" -> BSONDocument("$avg" ->"$score")
              )
            )
          )
        )

      val averageScore = BSONDocument(
        "aggregate" -> "scores",
        "pipeline" -> BSONArray(
          BSONDocument("$match" -> BSONDocument("$users.name" -> username)),
          BSONDocument(
            "$group" -> BSONDocument(
              "_id" -> "$users.name",
              "avgScore" -> BSONDocument("$avg" ->"$score")
              )
            )
          )
        )

      val avgResult: Future[BSONDocument] =  
      db.command(RawCommand(averageScorePerPers))

      avgResult.onComplete {

        case Success(score) => {

          println(s"---- Average scores ----")
/*
          val res: List[AvgScore] = score.elements.take(1).flatMap{
            case (name, value: BSONArray) =>
            value.values.map{case (a: BSONDocument) =>
              AvgScoreReader.read(a)
              case _ => null
            }

            case _ => Nil
          }.toList

            val userelem= res.filter(a=> a.name == username)
            if (!userelem.isEmpty) {
              context.parent ! userelem.head.toResultMessage()
            }
            else {context.parent ! AvgScore(null,0).toResultMessage()}
*/
        }

        case o => {
          log.info("The query failed!")
          println(o)

          context.parent ! DummyWorker.Abort
        } 
      }
    }*/
  }
