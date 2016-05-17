package me.reminisce.computing


import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.database._
import me.reminisce.model.ComputationMessages._

import reactivemongo.api.collections.bson._
import reactivemongo.bson.{BSONDocument, BSONArray}
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

import com.github.nscala_time.time.Imports._

object ComputationWorker {
  def props(database: DefaultDB):Props =
    Props(new ComputationWorker(database))
  }

class ComputationWorker(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._

  def receive: Receive = {
    case Compute(kind, userID) =>
      log.info(s"Compute stats $kind for user $userID")
      kind match {
        case StatisticKind.AverageScore => computeAverageScore(userID)
        case StatisticKind.CountCorrectQuestion => computeCountCorrectQuestion(userID)
        case StatisticKind.CountWinnerGame => computeCountWinnerGame(userID)
      }
  }

  def computeAverageScore(userID: String) : Unit = {
    //TODO
    context.parent ! AverageScore(0)
  }

  def computeCountCorrectQuestion(userID: String) : Unit = {
    //TODO
    context.parent ! CountCorrectQuestion(0, 0)
  }
  
  def computeCountWinnerGame(userID: String) : Unit = {
    
    import me.reminisce.model.DatabaseCollection
    // The name's field depends on the userID
    val idScores = "$" + userID + "_Scores"
 
    val unifiedQuery = BSONDocument(
      "aggregate" -> DatabaseCollection.gameTestCollection,
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

    val runner = Command.run(BSONSerializationPack)

    val s : Future[BSONDocument] = runner.apply(database, runner.rawCommand(unifiedQuery)).one[BSONDocument]
    s.onComplete{
      case Success(result) => 
        result.get("result") match { 
          case Some(array: BSONArray) =>
            array.get(0) match {
              case Some(doc: BSONDocument) =>
                val average = doc.getAs[Double]("averageScore")
                average match {
                  case Some(a) =>  context.parent ! AverageScore(a)
                  case _ =>  context.parent ! Abort
                }
               
              case e => log.info(s"No results for the user $userID")
            }
          case e =>
            log.info(s"Error: $e is not a BSONArray")
      }    
      case error =>
        log.info(s"The command has failed with error: $error")
    }    
  }
}
