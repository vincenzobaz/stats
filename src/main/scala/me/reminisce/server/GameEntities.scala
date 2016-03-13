package me.reminisce.server

import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.server.GameEntities.SubjectType.SubjectType
import me.reminisce.server.domain.RestMessage


object GameEntities {


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
                 )

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

  case class MultipleChoiceQuestion(
                                     kind: QuestionKind = QuestionKind.MultipleChoice,
                                     `type`: String,
                                     subject: Option[Subject],
                                     choices: List[Possibility],
                                     answer: Int) extends GameQuestion(kind, `type`)

  case class TimelineQuestion(

                               subject: Option[Subject],
                               min: String,
                               max: String,
                               default: String,
                               unit: String,
                               step: Int,
                               threshold: Int,
                               answer: String,
                               kind: QuestionKind = QuestionKind.Timeline,
                               `type`: String) extends GameQuestion(kind, `type`)

  case class OrderQuestion(
                            kind: QuestionKind = QuestionKind.Order,
                            `type`: String,
                            subject: Option[Subject],
                            choices: List[SubjectWithId],
                            answer: List[Int]
                          ) extends GameQuestion(kind, `type`)

  case class SubjectWithId(uId: Int, text: Option[String], subject: Option[Subject])


  case class Possibility(text: String, imageUrl: Option[String] = None, fbId: Option[String] = None, pageId: Option[Int] = None)

  case class GeolocationQuestion(subject: Option[Subject],
                                 range: Double,
                                 defaultLocation: Location,
                                 answer: Location,
                                 `type`: String,
                                 kind: QuestionKind = QuestionKind.Geolocation
                                ) extends GameQuestion(kind, `type`)

  case class Location(latitude: Double, longitude: Double)


}