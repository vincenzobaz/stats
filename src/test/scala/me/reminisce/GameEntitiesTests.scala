package me.reminisce

import me.reminisce.server.GameEntities
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import org.scalatest.FunSuite

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import me.reminisce.server.GameEntities._

/**
  * Created by sandra on 07/03/16.
  */
class GameEntitiesTests extends FunSuite{
  val content = Source.fromFile("/Users/sandra/Documents/EPFL/BachelorProject/boards.json").getLines.mkString("\n")
  val json = parse(content)



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
                // case "Misc" => ??? FIXME:
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
                // case "Misc" => ??? FIXME:
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

  }, {case _ => throw new UnsupportedOperationException
  }))
}
