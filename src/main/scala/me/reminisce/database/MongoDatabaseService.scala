package me.reminisce.database

import akka.actor.Props
import com.github.nscala_time.time.Imports._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.dummy._
import reactivemongo.api.DefaultDB
//import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.core.commands.GetLastError

import scala.concurrent.ExecutionContext.Implicits.global

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
  def props(userId: String, db: DefaultDB): Props =
    Props(new MongoDatabaseService(userId, db))

  case class Get(username: String)
  case class Result()  

  
}

class MongoDatabaseService(userId: String, db: DefaultDB) extends DatabaseService {


  def receive = {
  
    case Get(username) =>
      val service = context.actorOf(Props[DummyService])
      service ! DummyService.Search(username)

    case DummyService.Result(username, score) =>
      println(" Score : " + score)
      
  }

  /**
    * Converts and saves pages, extracts and saves the page likes
    * @param pages pages to work on
    */
  

}
