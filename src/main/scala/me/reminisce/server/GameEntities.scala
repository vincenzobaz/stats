package me.reminisce.server

import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.server.GameEntities.SubjectType.SubjectType
import me.reminisce.server.domain.RestMessage
import reactivemongo.bson._


object GameEntities {

  trait EntityMessage

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

  case class Move(row: Int, column: Int)

  case class Score(player: Int, score: Int)

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

// implicit BSONWriter and BSONreader
 

  implicit object SubjectTypeWriter extends BSONWriter[SubjectType, BSONString] {
    def write(t: SubjectType): BSONString = BSONString(t.toString)
  }
  implicit object SubjectTypeReader extends BSONReader[BSONValue, SubjectType] {
    def read(bson: BSONValue): SubjectType = bson match {
     case BSONString(s) => SubjectType.withName(s)
    }
  }
  
  implicit object QuestionKindWriter extends BSONWriter[QuestionKind, BSONString] {
    def write(t: QuestionKind): BSONString = BSONString(t.toString)
  }

  implicit object QuestionKindReader extends BSONReader[BSONValue, QuestionKind] {
    def read(bson: BSONValue): QuestionKind = bson match {
      case BSONString(s) => QuestionKind.withName(s)
    }
  }




  implicit object PostSubjectWriter extends BSONDocumentWriter[PostSubject] {
    def write(postSubject: PostSubject): BSONDocument =
    postSubject match {
      case TextPostSubject(text, t, from) => BSONDocument(
        "text" -> text,
        "type" -> t,
        "from" -> from 
        )
      case ImagePostSubject(text, imageUrl, facebookImageUrl, t, from) => BSONDocument(
        "text" -> text,
        "imageUrl" -> imageUrl,
        "facebookImageUrl" -> facebookImageUrl,
        "type" -> t,
        "from" -> from
        )
      case VideoPostSubject(text, thumbnailUrl, url, t, from) => BSONDocument(
        "text" -> text,
        "thumbnailUrl" -> thumbnailUrl,
        "url" -> url,
        "type" -> t,
        "from" -> from
        )
      case LinkPostSubject(text, thumbnailUrl, url, t, from) => BSONDocument(
        "text" -> text,
        "thumbnailUrl" -> thumbnailUrl,
        "url" -> url,
        "type" -> t,
        "from" -> from
        )
    }
  }

  implicit object PostSubjectReader extends BSONDocumentReader[PostSubject] {
    def read(doc: BSONDocument): PostSubject =
    SubjectType.withName(doc.getAs[String]("type").get) match {
      case SubjectType.TextPost => 
        val text = doc.getAs[String]("text").get
        val from = doc.getAs[FBFrom]("from")
        TextPostSubject(text, from = from) 
      case SubjectType.ImagePost => 
        val text = doc.getAs[String]("text").get
        val imageUrl = doc.getAs[String]("imageUrl")
        val fbUrl = doc.getAs[String]("facebookImageUrl")
        val from = doc.getAs[FBFrom]("from")
        ImagePostSubject(text, imageUrl, fbUrl, from = from)
      case SubjectType.VideoPost => 
        val text = doc.getAs[String]("text").get
        val thumbnailUrl = doc.getAs[String]("thumbnailUrl")
        val url = doc.getAs[String]("url")
        val from = doc.getAs[FBFrom]("facebookImageUrl")
        VideoPostSubject(text, thumbnailUrl, url, from = from)
      case SubjectType.LinkPost=>
        val text = doc.getAs[String]("text").get
        val thumbnailUrl = doc.getAs[String]("thumbnailUrl")
        val url = doc.getAs[String]("url")          
        val from = doc.getAs[FBFrom]("facebookImageUrl")
        LinkPostSubject(text, thumbnailUrl, url, from = from)
    }
  }

  implicit object SubjectReader extends BSONDocumentReader[Subject] {
    def read(doc: BSONDocument): Subject =
    SubjectType.withName(doc.getAs[String]("type").get) match {
      case SubjectType.PageSubject => 
        val name = doc.getAs[String]("name").get
        val pageId = doc.getAs[String]("pageId").get
        val photoUrl = doc.getAs[String]("photoUrl")
        PageSubject(name, pageId, photoUrl)
      case SubjectType.CommentSubject =>
        val comment = doc.getAs[String]("comment").get
        val post : Option[PostSubject] = doc.getAs[PostSubject]("post")
        CommentSubject(comment, post)
      case ps => PostSubjectReader.read(doc)
    }
  }

  implicit object SubjectWriter extends BSONDocumentWriter[Subject] {
    def write(subject: Subject): BSONDocument =
    subject match {
      case PageSubject(name, pageId, photoUrl, t) => BSONDocument(
        "name" -> name,
        "pageId" -> pageId,
        "photoUrl" -> photoUrl, 
        "type" -> t
        )    
      case CommentSubject(comment, post, t) => BSONDocument(
        "comment" -> comment,
        "post" -> post,
        "type" -> t 
        ) 
      case ps: PostSubject => PostSubjectWriter.write(ps)
    }
  }
  

  implicit val possibilityHandler: BSONHandler[BSONDocument, Possibility] = Macros.handler[Possibility]
  implicit val locationHandler: BSONHandler[BSONDocument, Location] = Macros.handler[Location]
  implicit val FBFromHandler: BSONHandler[BSONDocument, FBFrom] = Macros.handler[FBFrom]
  implicit val scoreHandler: BSONHandler[BSONDocument, Score] = Macros.handler[Score]
  implicit val moveHandler: BSONHandler[BSONDocument, Move] = Macros.handler[Move]

  implicit val geolocationQuestionHandler: BSONHandler[BSONDocument, GeolocationQuestion] = Macros.handler[GeolocationQuestion]

  implicit val pageSubjectHandler: BSONHandler[BSONDocument, PageSubject] = Macros.handler[PageSubject]
  implicit val commentSubjectHandler: BSONHandler[BSONDocument, CommentSubject] = Macros.handler[CommentSubject]
  implicit val TextPostSubjectHandler: BSONHandler[BSONDocument, TextPostSubject] = Macros.handler[TextPostSubject]
  implicit val ImagePostSubjectHandler: BSONHandler[BSONDocument, ImagePostSubject] = Macros.handler[ImagePostSubject]
  implicit val VideoPostSubjectHandler: BSONHandler[BSONDocument, VideoPostSubject] = Macros.handler[VideoPostSubject]
  implicit val LinkPostSubjectHandler: BSONHandler[BSONDocument, LinkPostSubject] = Macros.handler[LinkPostSubject]

  

  implicit object GameQuestionWriter extends BSONDocumentWriter[GameQuestion] {
    def write(question: GameQuestion): BSONDocument =
      question match {
        case MultipleChoiceQuestion(kind, t, subject, choices, answer) => BSONDocument (
          "kind" -> kind,
          "type" -> t,
          "subject" -> subject,
          "choices" -> choices,
          "answer" -> answer
          )
        case TimelineQuestion(subject, min, max, default, unit, step, threshold, answer, kind, t) => BSONDocument(
          "subject" -> subject,
          "min" -> min,
          "max" -> max,
          "default" -> default,
          "unit" -> unit,
          "step" -> step,
          "threshold" -> threshold,
          "answer" -> answer,

          "kind" -> kind,
          "type" -> t
          )
        case OrderQuestion(kind, t, choices, items, answer) => BSONDocument(
          "kind" -> kind,
          "type" -> t,
          "choices" -> choices,
          "items" -> items,
          "answer" -> answer
          )
        case GeolocationQuestion(subject, range, defaultLocation, answer, t, kind) => BSONDocument(
          "subject" -> subject,
          "range" -> range,
          "defaultLocation" -> defaultLocation,
          "answer" -> answer,
          "type" -> t,
          "kind" -> kind
          )
      }
  }

  implicit object GameQuestionReader extends BSONDocumentReader[GameQuestion] {
    def read(doc: BSONDocument) : GameQuestion = 
      QuestionKind.withName(doc.getAs[String]("kind").get) match {
        case QuestionKind.MultipleChoice => 
          val t = doc.getAs[String]("type").get
          val subject = doc.getAs[Subject]("subject")
          val choices = doc.getAs[List[Possibility]]("choices").get
          val answer = doc.getAs[Int]("answer").get
          MultipleChoiceQuestion(QuestionKind.MultipleChoice, `t`, subject, choices, answer)
        case QuestionKind.Timeline => 
          val subject = doc.getAs[Subject]("subject")
          val min = doc.getAs[String]("min").get
          val max = doc.getAs[String]("max").get
          val default = doc.getAs[String]("default").get
          val unit = doc.getAs[String]("unit").get
          val step = doc.getAs[Int]("step").get
          val threshold = doc.getAs[Int]("threshold").get
          val answer = doc.getAs[String]("answer").get
          val t = doc.getAs[String]("type").get
          TimelineQuestion(subject, min, max, default, unit, step, threshold, answer, QuestionKind.Timeline, `t`)
        case QuestionKind.Order => 
          val t = doc.getAs[String]("type").get
          val choices = doc.getAs[List[SubjectWithId]]("choices").get
          val items = doc.getAs[List[Item]]("items").get
          val answer = doc.getAs[List[Int]]("answer").get
          OrderQuestion(QuestionKind.Order, t, choices, items, answer)
        case QuestionKind.Geolocation => 
          val subject = doc.getAs[Subject]("subject")
          val range = doc.getAs[Double]("range").get
          val defaultLocation = doc.getAs[Location]("defaultLocation").get
          val answer = doc.getAs[Location]("answer").get
          val t = doc.getAs[String]("type").get
          GeolocationQuestion(subject, range, defaultLocation, answer, `t`, QuestionKind.Geolocation)
      }    
  }
 
  implicit val itemHandler: BSONHandler[BSONDocument, Item] = Macros.handler[Item]
  implicit val subjectIDHandler: BSONHandler[BSONDocument, SubjectWithId] = Macros.handler[SubjectWithId]
  implicit val tileHandler: BSONHandler[BSONDocument, Tile] = Macros.handler[Tile]
  implicit val boardHandler: BSONHandler[BSONDocument, Board] = Macros.handler[Board]

// Change the format of the Game that is stored in the DB
implicit object GameWriter extends BSONDocumentWriter[Game] {
    def write(game: Game) : BSONDocument = {
      val Game(id, 
        player1, player2, 
        player1Board, player2Board, 
        status, 
        playerTurn, 
        player1Scores, player2Scores,
        boardState,
        player1AvailableMoves, player2AvailableMoves,
        wonBy,
        creationTime) = game
      BSONDocument(
        "_id" -> id,
        "player1" -> player1,
        "player2" -> player2,
        s"${player1}_Board" -> player1Board,
        s"${player2}_Board" -> player2Board,
        "status" -> status,
        "playerTurn" -> playerTurn,
        s"${player1}_Scores"-> player1Scores,
        s"${player2}_Scores" -> player2Scores,
        "boardState" -> boardState,
        s"${player1}_AvailableMoves" -> player1AvailableMoves,
        s"${player2}_AvailableMoves" -> player2AvailableMoves,
        "wonBy" -> wonBy,
        "creationTime" -> creationTime
        )
    }
  }

 implicit object GameReader extends BSONDocumentReader[Game] {
    def read(doc: BSONDocument) : Game = {
      val id = doc.getAs[String]("_id").get
      val player1 = doc.getAs[String]("player1").get
      val player2 = doc.getAs[String]("player2").get
      val player1Board = doc.getAs[Board](s"${player1}_Board").get
      val player2Board = doc.getAs[Board](s"${player2}_Board").get
      val status = doc.getAs[String]("String").get
      val playerTurn = doc.getAs[Int]("playerTurn").get
      val player1Scores = doc.getAs[Int](s"${player1}_Scores").get
      val player2Scores = doc.getAs[Int](s"${player2}_Scores").get
      val boardState = doc.getAs[List[List[Score]]]("boardState").get
      val player1AvailableMoves = doc.getAs[List[Move]](s"${player1}_AvailableMoves").get
      val player2AvailableMoves = doc.getAs[List[Move]](s"${player2}_AvailableMoves").get
      val wonBy = doc.getAs[Int]("wonBy").get
      val creationTime = doc.getAs[Int]("creationTime").get
      Game(id, 
        player1, player2, 
        player1Board, player2Board, 
        status, 
        playerTurn, 
        player1Scores, player2Scores,
        boardState,
        player1AvailableMoves, player2AvailableMoves,
        wonBy,
        creationTime)
    }
  }
  /*
  implicit object GameWriter extends BSONDocumentWriter[Game] {
    def write(game: Game) : BSONDocument = {
      val Game(id, 
        player1, player2, 
        player1Board, player2Board, 
        status, 
        playerTurn, 
        player1Scores, player2Scores,
        boardState,
        player1AvailableMoves, player2AvailableMoves,
        wonBy,
        creationTime) = game
      BSONDocument(
        "_id" -> id,
        "player1" -> player1,
        "player2" -> player2,
        "player1Board" -> player1Board,
        "player2Board" -> player2Board,
        "status" -> status,
        "playerTurn" -> playerTurn,
        "player1Scores"-> player1Scores,
        "player2Scores" -> player2Scores,
        "boardState" -> boardState,
        "player1AvailableMoves" -> player1AvailableMoves,
        "player2AvailableMoves" -> player2AvailableMoves,
        "wonBy" -> wonBy,
        "creationTime" -> creationTime
        )
    }
  }
 implicit object GameReader extends BSONDocumentReader[Game] {
    def read(doc: BSONDocument) : Game = {
      val id = doc.getAs[String]("_id").get
      val player1 = doc.getAs[String]("player1").get
      val player2 = doc.getAs[String]("player2").get
      val player1Board = doc.getAs[Board]("player1Board").get
      val player2Board = doc.getAs[Board]("player2Board").get
      val status = doc.getAs[String]("String").get
      val playerTurn = doc.getAs[Int]("playerTurn").get
      val player1Scores = doc.getAs[Int]("player1Scores").get
      val player2Scores = doc.getAs[Int]("player2Scores").get
      val boardState = doc.getAs[List[List[Score]]]("boardState").get
      val player1AvailableMoves = doc.getAs[List[Move]]("player1AvailableMoves").get
      val player2AvailableMoves = doc.getAs[List[Move]]("player2AvailableMoves").get
      val wonBy = doc.getAs[Int]("wonBy").get
      val creationTime = doc.getAs[Int]("creationTime").get
      Game(id, 
        player1, player2, 
        player1Board, player2Board, 
        status, 
        playerTurn, 
        player1Scores, player2Scores,
        boardState,
        player1AvailableMoves, player2AvailableMoves,
        wonBy,
        creationTime)
    }*/
  
  

}