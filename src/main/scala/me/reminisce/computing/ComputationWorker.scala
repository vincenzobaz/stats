package me.reminisce.computing

import akka.actor._
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.server.GameEntities._
import me.reminisce.model.ComputationMessages._
import me.reminisce.statistics.StatisticEntities.IntervalKind.IntervalKind
import me.reminisce.statistics.StatisticEntities.QuestionsBreakDownKind.QuestionsBreakDownKind
import reactivemongo.bson.{BSONDocument, BSONArray}
import reactivemongo.api.collections.bson._
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.Command
import reactivemongo.api.BSONSerializationPack

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success
//import org.joda.time._
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

    case m => log.info(s"[CW] Unexpected message $m received")
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
      case o => log.info(s"[CW] Unexpected message $o received in ComputationWorker")
    }
  }

  def isComplete(userID: String, client: ActorRef, stat: StatsOnInterval, remaining: Int): Unit = {
    val newRemaining = remaining -1
    if (newRemaining == 0){
      client ! ResponseStatOnInterval(stat)
      context.become(waitingRequest)
    } else {
      context.become(waitingSubStats(client, userID, stat, newRemaining))
    }
  }

  def computeAmount(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    import me.reminisce.model.DatabaseCollection

    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                  ),
                "creationTime" -> BSONDocument(
                  "$gte" -> from.getMillis,
                  "$lt" -> to.getMillis
                  )
              )

    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).cursor[Game](
        ).collect[List]()

      s.onComplete{
        case Success(games)  =>
          client ! AmountStat(games.size)
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          client ! AmountStat(0)
    }
  }

  def computeWon(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    import me.reminisce.model.DatabaseCollection
    
    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                  ),
                "wonBy" -> userID,
                "creationTime" -> BSONDocument(
                  "$gte" -> from.getMillis,
                  "$lt" -> to.getMillis
                  )
              )

    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).cursor[Game](
        ).collect[List]()

      s.onComplete{
        case Success(games)  =>
          client ! WonStat(games.size)
        case f =>
          log.info(s"Failure while getting stats. Error: $f ")
          client ! WonStat(0)
    }
  }

  def computeLost(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    import me.reminisce.model.DatabaseCollection
    
    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                  ),
                "wonBy" -> BSONDocument("$ne" -> userID),
                "creationTime" -> BSONDocument(
                  "$gte" -> from.getMillis,
                  "$lt" -> to.getMillis
                  )
              )

    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).cursor[Game](
        ).collect[List]()

      s.onComplete{
        case Success(games)  =>
          client ! LostStat(games.size)
        case f => 
          log.info(s"Failure while getting stats. Error: $f ")
          client ! LostStat(0)
    }
  }

  def computeQuestionBreakDown(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    val getQuestionsBreakDownForKind: ((QuestionsBreakDownKind, List[Game]) => 
        QuestionsBreakDown) = (k: QuestionsBreakDownKind, games: List[Game]) => {
      val (total, correct) = games.foldLeft((0, 0)){
        case ((t, c), Game(_, p1, p2, p1b, p2b, _, _, _, _, _, _, _, _, _)) =>
          val tiles = if (p1 == userID) p1b.tiles else p2b.tiles
          val stats = tiles.foldLeft((0,0)) {
            case(acc2, Tile(questionKind, _, _, _, _, score, ans, dis)) =>
              if(k == QuestionsBreakDownKind.withName(questionKind) && ans && !dis)
                (acc2._1 + 3, acc2._1 + score)
              else
                acc2            
          }
          (t + stats._1, c + stats._2)
      }
      val percent: Double = if(total != 0) correct / total else 0.0
      QuestionsBreakDown(k, total, correct, percent)
    }

    import me.reminisce.model.DatabaseCollection
    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                  ),
                  "creationTime" -> BSONDocument(
                  "$gte" -> from.getMillis,
                  "$lt" -> to.getMillis
                  )
              )
    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).cursor[Game](
        ).collect[List]()

      s.onComplete{
        case Success(games)  =>
          if(!games.isEmpty){
            val l = QuestionsBreakDownKind.values.map(v => getQuestionsBreakDownForKind(v, games)).toList
              client ! QuestionBreakDownStat(l) 
            } else {
              client ! QuestionBreakDownStat(List())
            }
        case f => 
          log.info(s"Failure while getting stats. Error: $f ")
          client ! QuestionBreakDownStat(List())
    }
  }

  def computeGamesPlayedAgainst(client: ActorRef, userID: String, from: DateTime, to: DateTime) = {
    val battleSummary: ((String, List[Game]) => GamesPlayedAgainst) = (opponentID: String, plays: List[Game]) => {
      val (nb, won) = plays.foldLeft((0, 0)){
        case ((n, w), game) =>
          val userAs = if(game.player1 == userID) 1 else 2
            if(game.wonBy == userAs)
              (n + 1, w + 1)
            else 
              (n + 1, w)
          }
      GamesPlayedAgainst(opponentID, nb, won, nb - won)
    }
     
    import me.reminisce.model.DatabaseCollection

    val userScore = s"${userID}_Scores"
    val query = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                  ),
                  "creationTime" -> BSONDocument(
                  "$gte" -> from.getMillis,
                  "$lt" -> to.getMillis
                  )
              )
    val s: Future[List[Game]] = database[BSONCollection](
        DatabaseCollection.gameCollection).find(query).cursor[Game](
        ).collect[List]()

      s.onComplete{
        case Success(games)  =>
          if(!games.isEmpty){
            //Group games by opponentID
            val gameVsSomeone = games.groupBy{ g =>
              if(userID == g.player1) 
                g.player2
              else
                g.player1
              }
              val l: List[GamesPlayedAgainst] = gameVsSomeone.map{ case (id, v) => battleSummary(id, v)}.toList
              client ! GamesPlayedAgainstStat(l) 
            } else {
              client ! GamesPlayedAgainstStat(List())
            }
        case f => 
          log.info(s"Failure while getting stats. Error: $f ")
          client ! GamesPlayedAgainstStat(List())
    }  
  }
}
