package me.reminisce

import me.reminisce.server.GameEntities
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import org.scalatest.FunSuite

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import me.reminisce.server.GameEntities._
//import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL.WithDouble._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization

/**
  * Created by sandra on 07/03/16.
  */
class GameEntitiesTests extends FunSuite{
  implicit val formats = DefaultFormats + GameSerializer
  val content = Source.fromFile("/Users/sandra/Documents/EPFL/BachelorProject/boards.json").getLines.mkString("\n")
  val json = parse(content)

 // print(json.extract[Game])

  def extractQuestion(q:GameQuestion)  = q match {
    case TimelineQuestion( kind, tpe, subject, answer, min, max, default, unit, step, threshold) =>
      (("kind" -> kind.toString),
        ("`type`" -> tpe.toString),
        ("subject" -> subject.toString),
        ("answer" -> answer.toString),
        ("min" -> min.toString),
        ("max" -> max.toString),
        ("default" -> default.toString),
        ("unit" -> unit.toString),
        ("step" -> step.toString),
        ("threshold" -> threshold.toString)
        )
    case MultipleChoiceQuestion( kind, tpe, subject, choices, answer) =>
      ( ("kind" -> kind.toString),
        ("`type`" -> tpe.toString),
        ("subject" -> subject.toString), // Option ???
        ("choices" -> choices.toString() ),
        ("answer" -> answer.toString)
        )
    case GeolocationQuestion( subject, range, defaultLocation, answer, tpe, kind) =>
      (("subject" -> subject.toString),
        ("range" -> range.toString),
        ("defaultLocation" -> (("latitude"-> defaultLocation.latitude.toString),("longitude" -> defaultLocation.latitude.toString))),
        ("answer" -> (("latitude"-> answer.latitude.toString),("longitude" -> answer.latitude.toString))),
        ("`type`" -> tpe.toString),
        ("kind" -> kind.toString)
        )
    case OrderQuestion( kind, tpe, subject, choices, answer) =>
      (("kind" -> kind.toString),
        ("`type`" -> tpe.toString),
        ("subject" -> subject.toString),
        ("choices" -> choices.toString()),
        ("answer" -> answer.toString())
        )
  }
  object GameSerializer extends CustomSerializer[Game]( format => ({
    case JObject(
    JField("gameId", JString(gameId)) ::
      JField("player1Id", JString(player1Id)) ::
      JField("player2Id", JString(player2Id)) :: _) =>
      implicit val formats = DefaultFormats
      val player1Board = (json \ "player1Board") match {
        case JObject(JField("userId", JString(userId)) :: _) =>
          val tiles = (json \ "player1Board" \ "tiles") match {
            case JArray(t) => t map {
              tile => (tile \ "`type`").extract[String] match {
                case "MultipleChoice" => Tile(QuestionKind.MultipleChoice, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[MultipleChoiceQuestion],
                  (tile \ "question2").extract[MultipleChoiceQuestion],
                  (tile \ "question2").extract[MultipleChoiceQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])

                case "Timeline" => Tile(QuestionKind.Timeline, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[TimelineQuestion],
                  (tile \ "question2").extract[TimelineQuestion],
                  (tile \ "question2").extract[TimelineQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                case "Geolocation" => Tile(QuestionKind.Geolocation, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[GeolocationQuestion],
                  (tile \ "question2").extract[GeolocationQuestion],
                  (tile \ "question2").extract[GeolocationQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                case "Order" => Tile(QuestionKind.Order, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[OrderQuestion],
                  (tile \ "question2").extract[OrderQuestion],
                  (tile \ "question2").extract[OrderQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                // case "Misc" => ??? TODO:
                // case _ => //fail("Queston type unknown")
              }
            }
            case _ => Nil
          }
          Board(userId, tiles, (json \ "player1Board" \ "boardId").extract[String])
      }
      val player2Board = (json \ "player2Board") match {
        case JObject(JField("userId", JString(userId)) :: _) =>
          val tiles = (json \ "player2Board" \ "tiles") match {
            case JArray(t) => t map {
              tile => (tile \ "`type`").extract[String] match {
                case "MultipleChoice" => Tile(QuestionKind.MultipleChoice, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[MultipleChoiceQuestion],
                  (tile \ "question2").extract[MultipleChoiceQuestion],
                  (tile \ "question2").extract[MultipleChoiceQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])

                case "Timeline" => Tile(QuestionKind.Timeline, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[TimelineQuestion],
                  (tile \ "question2").extract[TimelineQuestion],
                  (tile \ "question2").extract[TimelineQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                case "Geolocation" => Tile(QuestionKind.Geolocation, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[GeolocationQuestion],
                  (tile \ "question2").extract[GeolocationQuestion],
                  (tile \ "question2").extract[GeolocationQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                case "Order" => Tile(QuestionKind.Order, (tile \ "tileId").extract[String],
                  (tile \ "question1").extract[OrderQuestion],
                  (tile \ "question2").extract[OrderQuestion],
                  (tile \ "question2").extract[OrderQuestion],
                  (tile \ "score").extract[BigInt].toInt, (tile \ "answered").extract[Boolean], (tile \ "disabled").extract[Boolean])
                // case "Misc" => ??? TODO:
                // case _ => //fail("Queston type unknown")
              }
            }
           // case _ => Nil
          }
          Board(userId, tiles, (json \ "player2Board" \ "boardId").extract[String])
      }
      val status = (json \ "status").extract[String]
      val playerTurn = (json \ "playerTurn").extract[BigInt].toInt
      val player1Scores = (json \ "player1Scores").extract[BigInt].toInt

      val player2Scores = (json \ "player2Scores").extract[BigInt].toInt
      val boardState = (json \ "boardState") match {
        case JArray(s) => s map {
          state => val st = (state \ "score") match {
            case JArray(sc) => sc map {
              score => Score((score \ "player").extract[BigInt].toInt, (score \ "score").extract[BigInt].toInt)
            }
          }
            s.asInstanceOf[State]
        }

      }
      val player1AvailableMoves = (json \ "player1AvailableMoves") match {
        case JArray(m) => m map {
          move => Move((move \ "row").extract[BigInt].toInt, (move \ "col").extract[BigInt].toInt)
        }
      }
      val player2AvailableMoves = (json \ "player2AvailableMoves") match {
        case JArray(m) => m map {
          move => Move((move \ "row").extract[BigInt].toInt, (move \ "col").extract[BigInt].toInt)
        }
      }
      val wonBy = (json \ "wonBy").extract[BigInt].toInt
      val creationTime = (json \ "creationTime").extract[BigInt].toInt

      GameEntities.Game(gameId, player1Id, player2Id, player1Board, player2Board, status, playerTurn,
        player1Scores,player2Scores, boardState, player1AvailableMoves, player2AvailableMoves, wonBy, creationTime)

  }, {case game: Game =>
      implicit val fmts = formats
      val player2AvailableMoves = game.player2AvailableMoves map {
        move => (("row" -> move.row),
          ("col" -> move.col))
      }

    val player1AvailableMoves = game.player2AvailableMoves map {
      move => (("row" -> move.row),
        ("col" -> move.col))
    }

    val boardState =  ???
    val player1Board = (("userId" -> game.player1Board.userId),
      ("tiles" -> (game.player1Board.tiles map {
        tile =>( ("`type`" -> tile.`type`),
          ("tileId" -> tile.tileId),
          ("question1" ->extractQuestion(tile.question1)),
          ("question2" ->extractQuestion(tile.question2)),
          ("question3" ->extractQuestion(tile.question3)),
          ("score" -> tile.score),
          ("answered" -> tile.answered),
          ("disabled" -> tile.disabled))})
      ))
    val player2Board = (("userId" -> game.player2Board.userId),
      ("tiles" -> (game.player2Board.tiles map {
        tile => (("`type`" -> tile.`type`.toString),
          ("tileId" -> tile.tileId.toString),
          ("question1" ->extractQuestion(tile.question1)),
          ("question2" ->extractQuestion(tile.question2)),
          ("question3" ->extractQuestion(tile.question3)),
          ("score" -> tile.score.toString),
          ("answered" -> tile.answered.toString),
          ("disabled" -> tile.disabled.toString))})
      ))

    val status = ("status" -> game.status)
    val playerTurn = ("playerTurn" -> game.playerTurn)
    val player1Scores = ("player1Scores" -> game.player1Scores)
    val player2Scores = ("player2Scores" -> game.player2Scores)
    val wonBy = ("wonBy" -> game.wonBy)
    val creationTime = ("creationTime" -> game.creationTime)
    val gameId = ("gameId" -> game.gameId)
    val player1Id = ("player1Id" -> game.player1Id)
    val player2Id = ("player2Id" -> game.player2Id )

    Serialization.write(player1Board)
  }))

}
