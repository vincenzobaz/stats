package me.reminisce.database

import akka.actor.Props
import me.reminisce.server.GameEntities._
import me.reminisce.database.MongoDatabaseService._
import me.reminisce.database.MongoDBEntities._
import me.reminisce.server.ApplicationConfiguration
import me.reminisce.dummy._
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONInteger}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.core.commands.GetLastError
import reactivemongo.core.commands._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }
import reactivemongo.api._
import reactivemongo.bson._

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

    case class Query(username: String)
    case class Insert(bson: BSONDocument)
    case class InsertEntity(entity: EntityMessage, collection: String)



  }

  class MongoDatabaseService(db: DefaultDB) extends DatabaseService {

    import MongoDatabaseService._
    import me.reminisce.server.GameEntities._


    def receive = {

      case Query(username) =>
        avgQuery(username)
    //  case Insert(bson) =>
    //    insertInDb(bson, "test") //TODO: one collection for GameBoard
      case InsertEntity(entity, collection) =>
        insertInDb(entity, collection)
    }

    def insertInDb(entity: EntityMessage, collection: String){
      entity match{
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
    }
  }
