package me.reminisce.server

import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.server.GameEntities.SubjectType.SubjectType
import me.reminisce.server.domain.RestMessage
import me.reminisce.dummy.DummyService._ 
import reactivemongo.bson._


object GameEntities {

  abstract sealed trait EntityMessage
 
  case class Game(_id: String,
                  player1: String,
                  player2: String,
                  player1Board: Board,
                  player2Board: Board,
                  status: String,
                  playerTurn: Int,
                  player1Scores: Int,
                  player2Scores: Int,
                  boardState: List[List[Score]],
                  player1AvailableMoves: List[Move],
                  player2AvailableMoves: List[Move],
                  wonBy: Int,
                  creationTime: Int
                 ) extends EntityMessage {
    override def toString(): String = s"GAME: players: $player1($player1Scores) vs $player2($player2Scores) : winner: $wonBy"
  }

  case class Board(userId: String, tiles: List[Tile], _id: String) extends RestMessage  

  case class Tile(`type`: String,
                  _id: String,
                  question1: Option[GameQuestion],
                  question2: Option[GameQuestion],
                  question3: Option[GameQuestion],
                  score: Int,
                  answered: Boolean,
                  disabled: Boolean) extends RestMessage

  

  object QuestionKind extends Enumeration {
    type QuestionKind = Value
    val MultipleChoice = Value("MultipleChoice")
    val Timeline = Value("Timeline")
    val Geolocation = Value("Geolocation")
    val Order = Value("Order")
    val Misc = Value("Misc")
    } 

  object SubjectType extends Enumeration {
    type SubjectType = Value
    val PageSubject = Value("Page")
    val TextPost = Value("TextPost")
    val ImagePost = Value("ImagePost")
    val VideoPost = Value("VideoPost")
    val LinkPost = Value("LinkPost")
    val CommentSubject = Value("Comment")    
  }
  //TO TEST
  implicit object SubectTypeWriter extends BSONWriter[SubjectType, BSONString] {
    def write(t: SubjectType): BSONString = BSONString(t.toString)
  }
   implicit object SubectTypeReader extends BSONReader[SubjectType, BSONString] {
    def read(bson: BSONValue): SubjectType = bson match {
    case BSONString(s) => SubjectType.withName(s)
    }
  }
   implicit object QuestionKindWriter extends BSONWriter[QuestionKind, BSONString] {
    def write(t: QuestionKind): BSONString = BSONString(t.toString)
  }
   implicit object QuestionKindReader extends BSONReader[QuestionKind, BSONString] {
    def read(bson: BSONValue): QuestionKind = bson match {
    case BSONString(s) => QuestionKind.withName(s)
    }
  }

  case class Move(row: Int,
                  column: Int)

  case class Score(player: Int,
                   score: Int)


  /**
    * Abstract subject, a subject represents a facebook item
    *
    * @param `type` type of the subject
    */
  abstract sealed class Subject(`type`: SubjectType)

  abstract sealed class PostSubject(`type`: SubjectType, text: String, from: Option[FBFrom]) extends Subject(`type`)

  case class PageSubject(name: String, pageId: String,
                         photoUrl: Option[String],
                         `type`: SubjectType = SubjectType.PageSubject) extends Subject(`type`)

  case class TextPostSubject(text: String, `type`: SubjectType = SubjectType.TextPost,
                             from: Option[FBFrom]) extends PostSubject(`type`, text, from)

  case class ImagePostSubject(text: String, imageUrl: Option[String], facebookImageUrl: Option[String],
                              `type`: SubjectType = SubjectType.ImagePost,
                              from: Option[FBFrom]) extends PostSubject(`type`, text, from)

  case class VideoPostSubject(text: String, thumbnailUrl: Option[String], url: Option[String],
                              `type`: SubjectType = SubjectType.VideoPost,
                              from: Option[FBFrom]) extends PostSubject(`type`, text, from)

  case class LinkPostSubject(text: String, thumbnailUrl: Option[String], url: Option[String],
                             `type`: SubjectType = SubjectType.LinkPost,
                             from: Option[FBFrom]) extends PostSubject(`type`, text, from)

  case class CommentSubject(comment: String, post: Option[PostSubject], `type`: SubjectType = SubjectType.CommentSubject) extends Subject(`type`)

  case class FBFrom(userId: String, userName: String)

  

  /**
    * Abstract game question
    *
    * @param kind   kind of question (See [[me.reminisce.server.GameEntities.QuestionKind]]
    * @param `type` type of question
    */
  abstract sealed class GameQuestion(kind: QuestionKind, `type`: String)

  case class MultipleChoiceQuestion(kind: QuestionKind = QuestionKind.MultipleChoice,
                                    `type`: String,
                                    subject: Option[Subject],
                                    choices: List[Possibility],
                                    answer: Int) extends GameQuestion(kind, `type`)

  case class TimelineQuestion(subject: Option[Subject],
                              min: String,
                              max: String,
                              default: String,
                              unit: String,
                              step: Int,
                              threshold: Int,
                              answer: String,
                              kind: QuestionKind = QuestionKind.Timeline,
                              `type`: String) extends GameQuestion(kind, `type`)

  case class OrderQuestion(kind: QuestionKind = QuestionKind.Order,
                           `type`: String,
                           choices: List[SubjectWithId],
                           items: List[Item],
                           answer: List[Int]
                          ) extends GameQuestion(kind, `type`)

  case class SubjectWithId(uId: Int, subject: Option[Subject])

  case class Item(id: Int, text: String, subject: Option[Subject])


  case class Possibility(text: String, imageUrl: Option[String], fbId: Option[String], pageId: Option[Int])

  case class GeolocationQuestion(subject: Option[Subject],
                                 range: Double,
                                 defaultLocation: Location,
                                 answer: Location,
                                 `type`: String,
                                 kind: QuestionKind = QuestionKind.Geolocation
                                ) extends GameQuestion(kind, `type`)

  case class Location(latitude: Double, longitude: Double)

// Generate implicit BSONWriter and BSONreader

  implicit val MultiChoiceQHandler = Macros.handler[MultipleChoiceQuestion]
  implicit val timelineQHandler = Macros.handler[TimelineQuestion]
  implicit val orderQHandler = Macros.handler[OrderQuestion]
  implicit val subjectIDHandler = Macros.handler[SubjectWithId]
  implicit val itemHandler = Macros.handler[Item]
  implicit val possibilityHandler = Macros.handler[Possibility]
  implicit val geoLocationHandler = Macros.handler[GeolocationQuestion]
  implicit val locationHandler = Macros.handler[Location]
  

  implicit val txtPostHandler = Macros.handler[TextPostSubject]
  implicit val imgPostHandler = Macros.handler[ImagePostSubject]
  implicit val videoPostHandler = Macros.handler[VideoPostSubject]
  implicit val linkPostHandler = Macros.handler[LinkPostSubject]
  implicit val commentPostHandler = Macros.handler[CommentSubject]
  implicit val FBFromHandler = Macros.handler[FBFrom]

  implicit val scoreHandler = Macros.handler[Score]
  implicit val moveHandler = Macros.handler[Move]
  
  implicit val tileHandler = Macros.handler[Tile]
  implicit val boardHandler = Macros.handler[Board]
  implicit val gameHandler = Macros.handler[Game]
  
}