package me.reminisce.computing

import akka.actor._
import me.reminisce.statistics.Stats._
import me.reminisce.server.GameEntities._
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.inserting.InsertionService
import me.reminisce.retrieving.RetrievingService
import me.reminisce.model.ComputationMessages._
import me.reminisce.model.RetrievingMessages._
import me.reminisce.model.Messages._
import me.reminisce.model.DatabaseCollection
import me.reminisce.statistics.Utils._
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocument, BSONArray, BSONString, BSONObjectID}
import reactivemongo.api.collections.bson._
import reactivemongo.api.commands.Command
import org.joda.time.DateTime
import scala.util.{Failure, Success}
import scala.concurrent.Future
import com.github.nscala_time.time.Imports._

object ComputationService {
  def props(database: DefaultDB): Props = 
    Props(new ComputationService(database))
}

class ComputationService(database: DefaultDB) extends Actor with ActorLogging {
  import me.reminisce.model.InsertionMessages._
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = waitingRequest
  val collectionGame = database[BSONCollection](DatabaseCollection.gameCollection)
  val collectionStats = database[BSONCollection](DatabaseCollection.statsCollection)

  def waitingRequest: Receive = {
    case ComputeStatistics(userId) =>
      compute(userId)
    case o => 
      log.info(s"Unexpected message $o received in computation service")
  }

  def compute( userId: String): Unit = {
        
    val userScore = s"${userId}_Scores"
    val queryGame = BSONDocument(
                "status" -> "ended",
                userScore -> BSONDocument(
                  "$exists" -> true
                )
              )
    val g : Future[List[Game]] = collectionGame.find(queryGame).cursor[Game]().collect[List]()
    g.onComplete{
        case Success(games) =>
          val stats = aggregate(games, userId)
          insertOrUpdate(userId, stats)
        case Failure(e) =>
          log.error(s"Could not reach the database: $e")
          context.parent ! Abort
      }
    }    

    def insertOrUpdate(userId: String, stats: StatsEntities) : Unit = {
        
      val now: DateTime = DateTime.now
      val midnightToday = new DateTime(now.getYear, now.getMonthOfYear, now.getDayOfMonth, 0 , 0 , 0)
      val queryStats = BSONDocument(
          "userId" -> userId
        )
      val s : Future[List[StatsEntities]] = collectionStats.find(queryStats).cursor[StatsEntities]().collect[List]()
      s.onComplete {
        case Success(existingStats) =>
          val todayStats = existingStats.filter(x => x.date > midnightToday)
          if(todayStats.isEmpty) {
            val future = collectionStats.insert(stats)
            future.onComplete {
              case Failure(e) => 
                context.parent ! Abort
              case Success(lastError) => 
                context.parent ! Done
            }
          } else {
            val selector = BSONDocument("_id" -> todayStats.head.id)
            val StatsEntities(id, userId, date, amount, win, lost, tie, rivals, questionsByType) = stats

            val modifier = BSONDocument(
                "$set" -> BSONDocument(
                "date" -> date,
                "amount" -> amount,
                "win" -> win,
                "lost" -> lost,
                "tie" -> tie,
                "rivals" -> rivals,
                "questionsByType" -> questionsByType))

            val futureUpdate = collectionStats.update(selector, modifier)
            futureUpdate.onComplete {
              case Success(lastError) =>
                context.parent ! Done
              case Failure(e) =>
                log.error(s"Failed to update stats: $e")
                context.parent ! Abort
            }
          }
        case Failure(e) =>
          log.error(s"Could not reach the database: $e")
          context.parent ! Abort
      }
    }

    def aggregate(games: List[Game], userId: String) : StatsEntities = {
      val (win, lost, tie, amount): (Int, Int, Int, Int) = games.foldLeft[(Int, Int, Int, Int)]((0, 0, 0, 0)){
        case ((w, l, t, a), Game(_, p1, p2, _, _, _, _, p1s, p2s, _, _, _, _, _)) =>        
          val (score, rival) = if (p1 == userId) (p1s, p2s) else (p2s, p1s)         
            if(score > rival) {
              (w + 1, l, t, a + 1)
            } else {
              if(score < rival){
                (w, l + 1, t, a + 1)
              } else {
                (w, l, t + 1, a + 1)
              }
            }
      }
      val rivals: Set[String] = games.foldLeft[Set[String]]((Set())){
        case (r, Game(_, p1, p2, _, _, _, _, _, _, _, _, _, _, _)) =>
          if (p1 == userId) r + p2 else r + p1
      }

      // val allQuestions = games.foldLeft[List[GameQuestion]](List()){
      //   case (t, Game(_, p1, p2, p1b, p2b, _, _, _, _, _, _, _, _, _)) =>

      //     if(userId == p1) {
      //       val tile = p1b.tile
      //       (t ++ List(tile.question1, tile.question2, tile.question3)
      //     }
      //     else{
      //       val tile = p2b.tile
      //       (t ++ List(tile.question1, tile.question2, tile.question3)
      //     } 
      // }

      val allQuestionsPairedWithScore: List[(Boolean, Double, GameQuestion)] = games.foldLeft[List[(Boolean, Double, GameQuestion)]](List[(Boolean, Double, GameQuestion)]()){
        case (t, Game(_, p1, p2, p1b, p2b, _, _, _, _, _, _, _, _, _)) =>

          if(userId == p1) {
            val tiles = p1b.tiles
            println(s"Number of tiles: ${tiles.length}")

            val ques = tiles.foldLeft[List[(Boolean, Double, GameQuestion)]](List()){
              case (l, Tile(_, _, q1, q2, q3, scr, answ, dis)) =>
                if(!(dis & !answ)){
                  println(q1)
                  val questions = List(q1, q2, q3)// remove None
                  val score: Double = scr / 3
                  questions.map(x => (answ, score, x))
                } else {
                  (l)
                }
              }
            (t ++ ques)
          }
          else {

            val tiles = p2b.tiles

            val ques = tiles.foldLeft[List[(Boolean, Double, GameQuestion)]](List()){
              case (l, Tile(_, _, q1, q2, q3, scr, answ, dis)) =>
                if(!(dis & !answ)){
                  val questions = List(q1, q2, q3) // remove None
                  val score: Double = scr / 3
                  questions.map(x => (answ, score, x))
                } else {
                  (l)
                }
              }            
            (t ++ ques)
          } 
      }

      val groups: Map[QuestionKind, List[(Boolean, Double, GameQuestion)]] = allQuestionsPairedWithScore.groupBy{case (a, s, q) => q.kind}

      val scoring = groups.map(t => (t._1, t._2.foldLeft[(Int, Double, Double, Int)]((0,0,0,0)){
        case ((a, c, w, av), (answer, score, question))   =>
          if(answer) {
            (a+1, c + score, w + (1-score),  av)
          } else {
            (a, c, w, av + 1)
          } 
        }          
      )).map{case (k, (a: Int, c: Double, w: Double, av: Int)) => (k, QuestionStats(a, c, w, av))}

      val questionsByType = QuestionsByType(
        scoring.getOrElse(QuestionKind.MultipleChoice, QuestionStats(0,0,0,0)),
        scoring.getOrElse(QuestionKind.Timeline, QuestionStats(0,0,0,0)),
        scoring.getOrElse(QuestionKind.Geolocation, QuestionStats(0,0,0,0)),
        scoring.getOrElse(QuestionKind.Order, QuestionStats(0,0,0,0))
        )

      val id = BSONObjectID.generate
      val stats = StatsEntities(id, userId, DateTime.now, amount, win, lost, tie, rivals, questionsByType)
      stats
    }

    def emptyStats(userId: String): StatsEntities = {
      StatsEntities(BSONObjectID.generate, userId, DateTime.now, 0, 0, 0, 0, Set(), emptyQuestionsByType())
    }
    def emptyQuestionsByType(): QuestionsByType = {
      val e = QuestionStats(0, 0, 0, 0)
      QuestionsByType(e, e, e, e)
    }
}
