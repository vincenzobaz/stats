package me.reminisce.database

import akka.actor.Props
import com.github.nscala_time.time.Imports._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.database.MongoDBEntities._
import me.reminisce.dummy._
import reactivemongo.api.DefaultDB
import me.reminisce.server.ApplicationConfiguration
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.core.commands.GetLastError
import reactivemongo.core.commands._

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.bson._
import scala.concurrent.Future
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

    case class Query(username: String)



  }

  class MongoDatabaseService(db: DefaultDB) extends DatabaseService {

    import MongoDatabaseService._

    def receive = {

      case Query(username) =>
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
        //println(score.toString)
        //score.elements.foreach(println)
        val res: List[AvgScore] = score.elements.take(1).flatMap{
          case (name, value: BSONArray) =>
            value.values.map{case (a: BSONDocument) =>
              AvgScoreReader.read(a)
              case _ => null
            }

          case _ => Nil
        }.toList
        
        //res.foreach(println)
        val userelem= res.filter(a=> a.name == username)
        if (!userelem.isEmpty) {
          context.parent ! userelem.head.toResultMessage()
        }
        else {context.parent ! AvgScore(null,0).toResultMessage()}
        
      }
    
    case o => {
      log.info("The query failed!")
      println(o)

      context.parent ! DummyWorker.Abort

    } 
  }

}

def executeQuery(query: BSONDocument, message: String){

  val usersCol = db.collection[BSONCollection](MongoDatabaseService.scoresCollection)

  val result: Future[List[BSONDocument]] =  
  usersCol.
  find(query). 
  cursor[BSONDocument].collect[List]()


  result.onComplete {
    case Success(score) => {
      log.info("success!")
      println(s"---- $message ----")
      score.foreach(doc => println(doc.get("name")))
          
        }
        case o => {
          log.info("The query failed!")

          context.parent ! DummyWorker.Abort
          
        }  
      }

    }

  /**
    * Converts and saves pages, extracts and saves the page likes
    * @param pages pages to work on
    */


  }
