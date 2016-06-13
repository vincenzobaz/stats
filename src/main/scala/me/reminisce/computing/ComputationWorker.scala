package me.reminisce.computing


import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.database._
import me.reminisce.model.ComputationMessages._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind

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
  def props(database: DefaultDB, kind: IntervalKind, ago: Int): Props = { 
    Props(new ComputationWorker(database, kind, ago))
  }
}

class ComputationWorker(database: DefaultDB, kind: IntervalKind, ago: Int) extends Actor with ActorLogging {
  
  def receive: Receive = waitingRequest

  def waitingRequest: Receive = {
    case ComputeStatsInInterval(userID, from, to) =>
    // TODO send a message for each subtype of stats.
    // Collect each response, and once we get all the parts, send to the manager

    SubStatisticKind.values.foreach{
      v =>
        val worker = context.actorOf(ComputationWorker.props(database, kind, ago))
        worker ! ComputeSubStat(userID, v, from, to)
    }
    context.become(waitingSubStats(sender, userID, StatsOnInterval(ago, 0, 0, 0, List(), List()), SubStatisticKind.values.size))
    
    case ComputeSubStat(userID, kind, from, to) =>
      val client = context.sender
      kind match {
        case SubStatisticKind.amount => computeAmount(client, userID, from, to)
        case SubStatisticKind.correct => computeCorrect(client, userID, from, to)
        case SubStatisticKind.percentCorrect => computePercentCorrect(client, userID, from, to)
        case SubStatisticKind.questionBreakDown => computeQuestionBreakDown(client, userID, from, to)
        case SubStatisticKind.gamePlayedAgainst => computeGamesPlayedAgainst(client, userID, from, to)
      }

    case m => log.info(s"Unexpected message $m received")
  }

  def waitingSubStats(client: ActorRef, userID: String, stats: StatsOnInterval, remaining: Int): Receive = {
    case AmountStat(nb) =>
      val StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, gamePlayedAgainst) = stats
      val newStats = StatsOnInterval(ago, nb, correct, percentCorrect, questionBreakDown, gamePlayedAgainst)
      isComplete(userID, client, newStats, remaining)
    case CorrectStat(nb) =>
      val StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, gamePlayedAgainst) = stats
      val newStats = StatsOnInterval(ago, amount, nb, percentCorrect, questionBreakDown, gamePlayedAgainst)
      isComplete(userID, client, newStats, remaining)
    case PercentCorrectStat(percent) =>
      val StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, gamePlayedAgainst) = stats
      val newStats = StatsOnInterval(ago, amount, correct, percent, questionBreakDown, gamePlayedAgainst)
      isComplete(userID, client, newStats, remaining)
    case QuestionBreakDownStat(questions) =>
      val StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, gamePlayedAgainst) = stats
      val newStats = StatsOnInterval(ago, amount, correct, percentCorrect, questions, gamePlayedAgainst)
      isComplete(userID, client, newStats, remaining)
    case GamesPlayedAgainstStat(games) =>
      val StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, gamePlayedAgainst) = stats
      val newStats = StatsOnInterval(ago, amount, correct, percentCorrect, questionBreakDown, games)
      isComplete(userID, client, newStats, remaining)
    case o => log.info(s"Unexpected message $o received in ComputationWorker")
  }

  def isComplete(userID: String, client: ActorRef, stat: StatsOnInterval, remaining: Int): Unit = {
    val newRemaining = remaining -1
    if (newRemaining == 0){
      client ! ResponseStatOnInterval(stat)
    } else {
      context.become(waitingSubStats(client, userID, stat, newRemaining))
    }
  }

// TODO : Aggregations !
  def computeAmount(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! AmountStat(0)
  }
  def computeCorrect(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! CorrectStat(0)
  }
  def computePercentCorrect(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! PercentCorrectStat(0)
  }
  def computeQuestionBreakDown(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! QuestionBreakDownStat(List())
  }
  def computeGamesPlayedAgainst(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! GamesPlayedAgainstStat(List())
  }

  /*def computeAverageScore(userID: String) : Unit = {
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
  }*/
}
