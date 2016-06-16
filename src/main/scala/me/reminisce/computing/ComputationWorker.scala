package me.reminisce.computing

import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.model.ComputationMessages._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind

import reactivemongo.bson.{BSONDocument, BSONArray}
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

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
      SubStatisticKind.values.foreach{
        v =>
          val worker = context.actorOf(ComputationWorker.props(database, kind, ago))
          worker ! ComputeSubStat(userID, v, from, to)
      }
      context.become(
        waitingSubStats(sender, userID, 
          StatsOnInterval(ago, 0, 0, 0, List(), List()), SubStatisticKind.values.size))
      
    case ComputeSubStat(userID, kind, from, to) =>
      val client = sender
      kind match {
        case SubStatisticKind.amount => computeAmount(client, userID, from, to)
        case SubStatisticKind.won => computeWon(client, userID, from, to)
        case SubStatisticKind.lost => computeLost(client, userID, from, to)
        case SubStatisticKind.questionBreakDown => computeQuestionBreakDown(client, userID, from, to)
        case SubStatisticKind.gamePlayedAgainst => computeGamesPlayedAgainst(client, userID, from, to)
      }

    case m => log.info(s"Unexpected message $m received")
  }

  def waitingSubStats(client: ActorRef, userID: String, stats: StatsOnInterval, remaining: Int): Receive = {
    val StatsOnInterval(ago, amount, won, lost, questionBreakDown, gamePlayedAgainst) = stats

    {
      case AmountStat(nb) =>
        val newStats = StatsOnInterval(ago, nb, won, lost, questionBreakDown, gamePlayedAgainst)
        isComplete(userID, client, newStats, remaining)
      case WonStat(nb) =>
        val newStats = StatsOnInterval(ago, amount, nb, lost, questionBreakDown, gamePlayedAgainst)
        isComplete(userID, client, newStats, remaining)
      case LostStat(nb) =>
        val newStats = StatsOnInterval(ago, amount, won, nb, questionBreakDown, gamePlayedAgainst)
        isComplete(userID, client, newStats, remaining)
      case QuestionBreakDownStat(questions) =>
        val newStats = StatsOnInterval(ago, amount, won, lost, questions, gamePlayedAgainst)
        isComplete(userID, client, newStats, remaining)
      case GamesPlayedAgainstStat(games) =>
        val newStats = StatsOnInterval(ago, amount, won, lost, questionBreakDown, games)
        isComplete(userID, client, newStats, remaining)
      case o => log.info(s"Unexpected message $o received in ComputationWorker")
    }
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
    println(from.getMillis)
    import me.reminisce.model.DatabaseCollection
    val idScores = "$" + userID + "_Scores"
    // TODO add the interval condition
    val unifiedQuery = BSONDocument(
      "aggregate" -> DatabaseCollection.gameCollection,
      "pipeline" -> BSONArray(
        BSONDocument(
          "$match" -> BSONDocument(
            "status" -> "ended",
            "creationTime" -> BSONDocument("$gt" -> from.getMillis)/*,
            "creationTime" -> BSONDocument("$lt" -> from.getMillis)*/
            )
        ), 
        BSONDocument(
          "$group" -> BSONDocument(
            "_id" -> idScores,
            "count" -> BSONDocument("$sum" -> 1)
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
                val count = doc.getAs[Int]("count")
                count match {
                  case Some(a) =>  client ! AmountStat(a)
                  case _ =>  client ! AmountStat(0)
                }               
              case e => 
                client ! AmountStat(0)
                log.info(s"No results for the user $userID")
            }
          case e =>
            client ! AmountStat(0)
            log.info(s"Error: $e is not a BSONArray")
        }
      case error =>
        client ! AmountStat(0)
        log.info(s"The command has failed with error: $error")
    }
  }

  def computeWon(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! WonStat(0)
  }

  def computeLost(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! LostStat(0)
  }

  def computeQuestionBreakDown(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! QuestionBreakDownStat(List())
  }

  def computeGamesPlayedAgainst(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    client ! GamesPlayedAgainstStat(List())
  }
}
