package me.reminisce

import me.reminisce.server.GameEntities.TimeUnit
import me.reminisce.server.GameEntities.SpecificQuestionType
import me.reminisce.server.GameEntities.SpecificQuestionType.SpecificQuestionType
import me.reminisce.server.GameEntities.TimeUnit.TimeUnit
import org.scalatest.FunSuite
import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import me.reminisce.server.GameEntities._

class GameEntitiesTests extends FunSuite{
  implicit val formats = DefaultFormats + new GameSerializer//Serialization.formats(NoTypeHints) //+ new GameSerializer
  val content = Source.fromFile("/Users/sandra/Documents/EPFL/BachelorProject/boards.json").getLines.mkString("\n")
  val json = parse(content)
  //print(json)
 json.extract[Game]

  def transformQuestion(q:GameQuestion)  = q match {
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

  def findTpe(tpe : String): SpecificQuestionType = tpe match {
    case "TLWhenDidYouShareThisPost" => SpecificQuestionType.TLWhenDidYouShareThisPost
    case "TLWhenDidYouLikeThisPage" =>  SpecificQuestionType.TLWhenDidYouLikeThisPage
    case "GeoWhatCoordinatesWereYouAt" => SpecificQuestionType.GeoWhatCoordinatesWereYouAt
    case "MCWhoMadeThisCommentOnYourPost" => SpecificQuestionType.MCWhoMadeThisCommentOnYourPost
    case "MCWhichPageDidYouLike" => SpecificQuestionType.MCWhichPageDidYouLike
    case "MCWhoLikedYourPost" => SpecificQuestionType.MCWhoLikedYourPost
    case "ORDPageLikes" => SpecificQuestionType.ORDPageLikes
    case "ORDPostCommentsNumber" => SpecificQuestionType.ORDPostCommentsNumber
    case "ORDPostLikesNumber" => SpecificQuestionType.ORDPostLikesNumber
    case "ORDPostTime" => SpecificQuestionType.ORDPostTime
    case "ORDPageLikeTime" => SpecificQuestionType.ORDPostTime
    case _ => throw new IllegalArgumentException("Unexpected argument.")
  }

  def findUnit(tpe:String):TimeUnit = tpe match {
    case "Day" => TimeUnit.Day
    case "Week" => TimeUnit.Week
    case "Month" => TimeUnit.Month
    case "Year" => TimeUnit.Year
    case _ => throw new IllegalArgumentException("Unexpected argument.")
  }

  def extractSubject(tpe: String,s:JObject /*s:List[(String,JValue)]*/) : Option[Subject] = tpe match {
    case "TextPost" => Some(TextPostSubject((s \ "text").extract[String]))
    case "ImagePost" => Some(ImagePostSubject((s \ "text").extract[String], (s \ "imageUrl").extractOpt[String],(s \ "facebookImageUrl").extractOpt[String]))
    case "VideoPost" => Some(VideoPostSubject((s \ "text").extract[String], (s \ "thumbnailUrl").extractOpt[String],(s \ "url").extractOpt[String]))
    case "LinkPost" => Some(LinkPostSubject((s \ "text").extract[String],(s \ "thumbnailUrl").extractOpt[String],(s \ "url").extractOpt[String]))
    case "Page" => Some(PageSubject((s \ "name").extract[String], (s \ "pageId").extract[String], (s \ "photoUrl").extractOpt[String]))
    case "Comment" => Some(CommentSubject((s \ "comment").extract[String], ???))
    case _ => throw new IllegalArgumentException("Unexpected argument.")
  }

  def extractPossibilites(choices:List[JValue]): List[Possibility] = choices map {
    choice => Possibility((choice \ "text").extract[String], (choice \ "imageUrl").extractOpt[String],
      (choice \ "fbId").extractOpt[String],(choice \ "pageId").extractOpt[Int] )
  }

  def extractChoices(choices : List[JValue], tpe : String) = choices map {
    choice => SubjectWithId((choice \ "subId").extract[Int],(choice \ "text").extract[String],
      ???)
  }

  def extractAnswer(answers:List[JValue]) : List[Int]= answers map {
    answer => answer.extract[Int]
  }

  def extractLocation(l:List[(String,JValue)]):Location = l match {
    case JField("latitude", JDouble(latitude)) :: JField("longitude",JDouble(longitude))::_ => Location(latitude,longitude)
    case _ => throw new IllegalArgumentException("Unexpected argument.")
  }

  def extractQuestion(tpe: String, q:List[(String,JValue)]): GameQuestion = tpe match {
    case "MultipleChoice" => q match {
      case JField("kind", JString(kind))::JField("`type`",JString(tpe))::JField("subject",JObject(s))::
        JField("choices",JArray(choices))::JField("answer",JInt(answer))::_ =>
        MultipleChoiceQuestion(QuestionKind.MultipleChoice,findTpe(tpe),extractSubject(tpe,JObject(s)),extractPossibilites(choices),answer.toInt)
    }
    case "Timeline" => q match {
      case JField("kind", JString(kind))::JField("`type`",JString(tpe))::JField("subject",JObject(s))::
        JField("answer",JString(answer))::JField("min",JString(min)):: JField("max",JString(max))::JField("default",JString(default))::
        JField("unit",JString(unit))::JField("step",JInt(step)):: JField("threshold",JInt(threshold))::_ =>
          TimelineQuestion(QuestionKind.Timeline,findTpe(tpe),extractSubject(tpe,JObject(s)),answer,min,max,default,findUnit(unit),step.toInt,threshold.toInt)

    }
    case "Geolocation" => q match {
      case JField("subject",JObject(s)) :: JField("range",JDouble(range)) :: JField("defaultLocation",JObject(loc))::
        JField("answer",JObject(answer))::JField("`type`",JString(tpe))::JField("kind",JString(kind))::_ =>
            GeolocationQuestion(extractSubject(tpe,JObject(s)),range,extractLocation(loc),extractLocation(answer),findTpe(tpe),QuestionKind.Geolocation)
    }
    case "Order" => q match {
      case JField("kind", JString(kind))::JField("`type`",JString(tpe))::JField("subject",JObject(s))::
        JField("choices",JArray(choices))::JField("answer",JArray(answer))::_ =>
        OrderQuestion(QuestionKind.Order,findTpe(tpe),extractSubject(tpe,JObject(s)),extractChoices(choices,tpe),extractAnswer(answer))
    }
    case _ => throw new IllegalArgumentException("Unexpected argument.")

  }

  class GameSerializer extends CustomSerializer[Game]( format => ({
    case JObject(
      JField("_id", JString(gameId)) ::
      JField("player1", JString(player1Id)) ::
      JField("player2", JString(player2Id)) ::
      JField("player1Board",JObject(player1Board))::
      JField("player2Board",JObject(player2Board))::
      JField("status",JString(status))::
      JField("playerTurn",JInt(playerTurn))::
      JField("player1Scores",JInt(player1Scores))::
      JField("player2Scores",JInt(player2Scores))::
      JField("boardState",JArray(boardState))::
      JField("player1AvailableMoves",JArray(player1AvailableMoves))::
      JField("player2AvailableMoves",JArray(player2AvailableMoves))::
      JField("wonBy",JInt(wonBy))::
      JField("creationTime", JInt(creationTime))::_) =>
      implicit val formats = DefaultFormats
      val player1Board = (json \ "player1Board") match {
        case JObject(JField("userId", JString(userId)) :: _) =>
          val tiles = (json \ "player1Board" \ "tiles") match {
            case JArray(t) => t map {
              tile => tile match {
                case JObject(JField("_id",JString(tileId)) :: JField("`type`",JString(tpe))::
                  JField("question1",JObject(q1))::JField("question2",JObject(q2))::JField("question3",JObject(q3))::
                  JField("score", JInt(score))::JField("answered", JBool(answered))::JField("disabled", JBool(disabled))::_) =>
                  Tile(QuestionKind.MultipleChoice, tileId,
                    extractQuestion(tpe,q1),
                    extractQuestion(tpe,q2),
                    extractQuestion(tpe,q3),
                    score.toInt, answered, disabled)
                case _ => throw new IllegalArgumentException("Unexpected argument.")
              }
            }
            // case _ => Nil
          }
          Board(userId, tiles, (json \ "player2Board" \ "boardId").extract[String])
      }
      val player2Board = (json \ "player2Board") match {
        case JObject(JField("userId", JString(userId)) :: _) =>
          val tiles = (json \ "player2Board" \ "tiles") match {
            case JArray(t) => t map {
              tile => tile match {
                case JObject(JField("_id",JString(tileId)) :: JField("`type`",JString(tpe))::
                JField("question1",JObject(q1))::JField("question2",JObject(q2))::JField("question3",JObject(q3))::
                JField("score", JInt(score))::JField("answered", JBool(answered))::JField("disabled", JBool(disabled))::_) =>
                Tile(QuestionKind.MultipleChoice, tileId,
                  extractQuestion(tpe,q1),
                  extractQuestion(tpe,q2),
                  extractQuestion(tpe,q3),
                  score.toInt, answered, disabled)
              }
            }
           // case _ => Nil
          }
          Board(userId, tiles, (json \ "player2Board" \ "boardId").extract[String])
      }
      //val status = (json \ "status").extract[String]
      //val playerTurn = (json \ "playerTurn").extract[BigInt].toInt
      //val player1Scores = (json \ "player1Scores").extract[BigInt].toInt

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

      Game(gameId, player1Id, player2Id, player1Board, player2Board, status, playerTurn.toInt,
        player1Scores.toInt,player2Scores.toInt, boardState, player1AvailableMoves, player2AvailableMoves, wonBy.toInt, creationTime.toInt)

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

    val boardState =  game.boardState map {
      state => ("score" -> (state.score map {
        s =>( ("player" -> s.player),
          ("score" -> s.score))
      }))
    }
    val player1Board = (("userId" -> game.player1Board.userId),
      ("tiles" -> (game.player1Board.tiles map {
        tile =>( ("`type`" -> tile.`type`.toString),
          ("tileId" -> tile.tileId),
          ("question1" ->transformQuestion(tile.question1)),
          ("question2" ->transformQuestion(tile.question2)),
          ("question3" ->transformQuestion(tile.question3)),
          ("score" -> tile.score.toString),
          ("answered" -> tile.answered.toString),
          ("disabled" -> tile.disabled.toString))})
      ))
    val player2Board = (("userId" -> game.player2Board.userId),
      ("tiles" -> (game.player2Board.tiles map {
        tile => (("`type`" -> tile.`type`.toString),
          ("tileId" -> tile.tileId.toString),
          ("question1" ->transformQuestion(tile.question1)),
          ("question2" ->transformQuestion(tile.question2)),
          ("question3" ->transformQuestion(tile.question3)),
          ("score" -> tile.score.toString),
          ("answered" -> tile.answered.toString),
          ("disabled" -> tile.disabled.toString))})
      ))

    val status = ("status" -> game.status)
    val playerTurn = ("playerTurn" -> game.playerTurn.toString)
    val player1Scores = ("player1Scores" -> game.player1Scores.toString)
    val player2Scores = ("player2Scores" -> game.player2Scores.toString)
    val wonBy = ("wonBy" -> game.wonBy.toString)
    val creationTime = ("creationTime" -> game.creationTime.toString)
    val gameId = ("gameId" -> game.gameId)
    val player1Id = ("player1Id" -> game.player1Id)
    val player2Id = ("player2Id" -> game.player2Id )
    throw new RuntimeException("No serializing") //TODO:

  }))
  /*class TileSerializer extends CustomSerializer[Tile]( format => ({
    case JObject(JField("_id",JString(tileId)) :: JField("`type`",JString(tpe))::
      JField("question1",JObject(q1))::JField("question2",JObject(q2))::JField("question3",JObject(q3))::
      JField("score", JInt(score))::JField("answered", JBool(answered))::JField("disabled", JBool(disabled))::_) =>
      Tile(QuestionKind.MultipleChoice, tileId,
        extractQuestion(tpe,q1),
        extractQuestion(tpe,q2),
        extractQuestion(tpe,q3),
        score.toInt, answered, disabled)
  },{case _ => throw new RuntimeException("No serializing")}))*/


}
