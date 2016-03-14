package me.reminisce.database

import akka.actor.Props
import com.github.nscala_time.time.Imports._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.dummy._
import reactivemongo.api.DefaultDB
import me.reminisce.server.ApplicationConfiguration
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.core.commands.GetLastError

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
      
      val query = BSONDocument(
        "name" -> username)
      val emptyquery = BSONDocument()

      val scorePerson: Future[List[BSONDocument]] =  
        usersCol.
        find(emptyquery). 
        cursor[BSONDocument].collect[List]()
      
      
      scorePerson.onComplete {
        case Success(score) => {
          log.info("success!")
          println(score)
          score.foreach(doc => println(doc.get("name")))
          context.parent ! DummyService.Result(username, 123634)
        }
        case o => {
          log.info("The query failed!")
          context.parent ! DummyWorker.Abort
        }  
      }

      
      log.info("query accomplished")
     
      
  }

  /**
    * Converts and saves pages, extracts and saves the page likes
    * @param pages pages to work on
    */
  

}
