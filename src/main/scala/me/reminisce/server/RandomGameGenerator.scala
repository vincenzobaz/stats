package me.reminisce.server

import me.reminisce.server.GameEntities.QuestionKind.QuestionKind
import me.reminisce.server.GameEntities.SubjectType.SubjectType
import me.reminisce.server.GameEntities._

import scala.util.Random

class RandomGameGenerator {

  object SpecificQuestionType extends Enumeration {
    type SpecificQuestionType = Value
    val TLWhenDidYouShareThisPost = Value("TLWhenDidYouShareThisPost")
    val TLWhenDidYouLikeThisPage = Value("TLWhenDidYouLikeThisPage")
    val GeoWhatCoordinatesWereYouAt = Value("GeoWhatCoordinatesWereYouAt")
    val MCWhoMadeThisCommentOnYourPost = Value("MCWhoMadeThisCommentOnYourPost")
    val MCWhichPageDidYouLike = Value("MCWhichPageDidYouLike")
    val MCWhoLikedYourPost = Value("MCWhoLikedYourPost")
    val ORDPageLikes = Value("ORDPageLikes")
    val ORDPostCommentsNumber = Value("ORDPostCommentsNumber")
    val ORDPostLikesNumber = Value("ORDPostLikesNumber")
    val ORDPostTime = Value("ORDPostTime")
    val ORDPageLikeTime = Value("ORDPageLikeTime")
  }

  val idlength = 10
  val names = List("Sandra", "Sarah", "Roger Küng", "Christian M. Schmid", "Andrea Blättler", "Tristan Overney", "Rose Anna K", "Sacha Vost", "Bianca Egli Uche")
  val userName = names(Random.nextInt(names.length))
  val userID = Random.nextInt(100) * 100

  val textPost = List("Have a nice evening", "Thanks to everyone.", "Don't you just love those beta oxidation potentials.",
    "Never thought it would be that cold in San Francisco...", "Predestination is one of the few examples where the time travel paradox is well implemented...",
    "is very tired in his apparment in Montreal", "is still freaked out", "is hungry", "is feeling happy", "is feeling sad", "is feeling loved")
  val imagePost = List("Roger Küng shared Trust Me, I'm an \\\"Engineer\\\"'s photo.", "Sandra added 33 new photos to the album: Asia",
    "Roger Küng added 9 new photos to the album: Barcelona.", "Sarah added 9 new photos to the album: Milan.", "Roger Küng added 7 new photos to the album: Coaching.",
    "Sarah shared Basel photo")
  val linkPost = List("Good to know...", "Roger Küng shared a link.", "Rebuild a human by placing atoms at the correct locations.", "Can't believe this myth is still alive...",
    "Sarah shared a link", "Swiss best universities.", "Top 5 songs", "Top 5 hotels.")
  val pageSubject = List("Perfume", "BuzzFeed", "One Man Left", "Theatersport Improphil Luzern", "Code School", "Coursera", "Archon", "Theater Aeternam", "Stack Overflow", "reddit", "The 4400")
  val commentPost = List("I just noticed that.", "Nice.", "Cool", "I didn't know", "The best", "Happy birthday", "Too bad", "Great", "I'm so sorry.")
  val videoPost = List("Roger Küng shared a link.", "Sandra shared a link.", "Roger Küng was tagged in a video.", "Sarah was tagged in a video.", "Sandra posted a video")


  def createGame(): Game = {
    val player1ID = Random.alphanumeric.take(idlength).mkString
    val player2ID = Random.alphanumeric.take(idlength).mkString
    new Game(Random.alphanumeric.take(idlength).mkString, player1ID, player2ID, createBoard(player1ID), createBoard(player2ID), "ended", Random.nextInt(2) + 1,
      Random.nextInt(10), Random.nextInt(10), boardState(), availableMoves(), availableMoves(), Random.nextInt(2) + 1, Random.nextInt(100))
  }

  def createBoard(id: String): Board = {
    new Board(id, createTiles(), Random.alphanumeric.take(idlength).mkString)
  }

  def createTiles(): List[Tile] = {
    val nbTiles = Random.nextInt(5) + 1 // we have at least 1 tile
    val qk = pickRandomQuestionKind()

    (for (i <- 0 to nbTiles) yield new Tile(qk.toString, Random.alphanumeric.take(idlength).mkString, Some(createQuestion(qk)), Some(createQuestion(qk)),
      Some(createQuestion(qk)), Random.nextInt(10), Random.nextBoolean(), Random.nextBoolean())).toList
  }

  def boardState(): List[List[Score]] = {
    (for (i <- 0 to 1)
      yield (for (i <- 0 to 2) yield new Score(Random.nextInt(3), Random.nextInt(10))).toList
      ).toList
  }

  def availableMoves(): List[Move] = {
    (for (i <- 0 to 2) yield new Move(Random.nextInt(3), Random.nextInt(3))).toList
  }

  def pickRandomQuestionKind(): QuestionKind = {
    QuestionKind(Random.nextInt(QuestionKind.maxId))
  }

  def pickRandomSpecificQuestionType(): SpecificQuestionType.Value = {
    SpecificQuestionType(Random.nextInt(SpecificQuestionType.maxId))
  }

  def createQuestion(qk: QuestionKind): GameQuestion = qk match {
    case QuestionKind.Geolocation => new GeolocationQuestion(createSubject(), Random.nextDouble(), createLocation(), createLocation(),
      pickRandomSpecificQuestionType().toString, QuestionKind.Geolocation)
    case QuestionKind.MultipleChoice => val nbChoices = Random.nextInt(10) + 1 //at least one choice
      new MultipleChoiceQuestion(QuestionKind.MultipleChoice, pickRandomSpecificQuestionType().toString, createSubject(), createPossibilites(nbChoices),
        Random.nextInt(nbChoices))
    case QuestionKind.Order => val nbChoices = Random.nextInt(10)
      new OrderQuestion(QuestionKind.Order, createSubject().toString, createChoices(nbChoices), createItems(nbChoices), Seq.fill(nbChoices)(Random.nextInt(4)).toList)
    case QuestionKind.Timeline => new TimelineQuestion(createSubject(), Random.alphanumeric.take(10).mkString, Random.alphanumeric.take(10).mkString, Random.alphanumeric.take(10).mkString,
      Random.alphanumeric.take(10).mkString, Random.nextInt(10), Random.nextInt(10), Random.alphanumeric.take(10).mkString, QuestionKind.Timeline, pickRandomSpecificQuestionType().toString)
    case QuestionKind.Misc => createQuestion(pickRandomQuestionKind())
  }

  def createSubject(): Option[Subject] = {
    val subType = pickRandomSubjectType()
    subType match {
      case SubjectType.CommentSubject => Some(new CommentSubject(pick(SubjectType.CommentSubject), None, SubjectType.CommentSubject))
      case SubjectType.ImagePost => Some(new ImagePostSubject(pick(SubjectType.ImagePost), Some(createRandomURL()), Some(createRandomURL()),
        SubjectType.ImagePost, Some(createFBFrom())))
      case SubjectType.LinkPost => Some(new LinkPostSubject(pick(SubjectType.LinkPost), Some(createRandomURL()), Some(createRandomURL()),
        SubjectType.LinkPost, Some(createFBFrom())))
      case SubjectType.PageSubject => Some(new PageSubject(pick(SubjectType.PageSubject), Random.alphanumeric.take(10).mkString, Some(createRandomURL()),
        SubjectType.PageSubject))
      case SubjectType.TextPost => Some(new TextPostSubject(pick(SubjectType.TextPost), SubjectType.TextPost, Some(createFBFrom())))
      case SubjectType.VideoPost => Some(new VideoPostSubject(pick(SubjectType.VideoPost), Some(createRandomURL()), Some(createRandomURL()),
        SubjectType.VideoPost, Some(createFBFrom())))
    }
  }

  def createFBFrom(): FBFrom = {
    new FBFrom(userID.toString, userName)
  }

  def createItems(nbItems: Int): List[Item] = {
    (for (i <- 1 to nbItems)
      yield {
        val subject = createSubject()
        subject.get match {
          case CommentSubject(comment, _, _) => new Item(Random.nextInt(100), comment, subject)
          case ImagePostSubject(text, _, _, _, _) => new Item(Random.nextInt(100), text, subject)
          case LinkPostSubject(text, _, _, _, _) => new Item(Random.nextInt(100), text, subject)
          case PageSubject(name, _, _, _) => new Item(Random.nextInt(100), name, subject)
          case TextPostSubject(text, _, _) => new Item(Random.nextInt(100), text, subject)
          case VideoPostSubject(text, _, _, _, _) => new Item(Random.nextInt(100), text, subject)
        }
      }).toList
  }

  def createPossibilites(nbChoices: Int): List[Possibility] = {
    (for (i <- 1 to nbChoices) yield createPossibility()).toList
  }

  def createChoices(nbChoices: Int): List[SubjectWithId] = {
    val listOfChoices = for (i <- 1 to nbChoices) yield createSubjectWihId()
    listOfChoices.toList
  }

  def createSubjectWihId(): SubjectWithId = {
    new SubjectWithId(Random.nextInt(100), createSubject())
  }

  def createPossibility(): Possibility = {
    new Possibility(names(Random.nextInt(names.length)), Some(createRandomURL()), Some(Random.alphanumeric.take(10).mkString), Some(Random.nextInt(Integer.MAX_VALUE)))
  }

  def createLocation(): Location = {
    new Location(Random.nextDouble(), Random.nextDouble())
  }

  def pickRandomSubjectType(): SubjectType = {
    SubjectType(Random.nextInt(SubjectType.maxId))
  }

  def pick(subType: SubjectType): String = {
    subType match {
      case SubjectType.CommentSubject => commentPost(Random.nextInt(commentPost.length))
      case SubjectType.ImagePost => imagePost(Random.nextInt(imagePost.length))
      case SubjectType.LinkPost => linkPost(Random.nextInt(linkPost.length))
      case SubjectType.PageSubject => pageSubject(Random.nextInt(pageSubject.length))
      case SubjectType.TextPost => textPost(Random.nextInt(textPost.length))
      case SubjectType.VideoPost => videoPost(Random.nextInt(videoPost.length))

    }
  }

  def createRandomURL(): String = {
    "https://www.facebook.com/" + Random.alphanumeric.take(20).mkString
  }
}
