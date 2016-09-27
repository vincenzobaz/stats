package me.reminisce

import java.io.InputStream

import me.reminisce.server.GameEntities._
import me.reminisce.server.{QuestionSerializer, SubjectSerializer}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.FunSuite

class GameEntitiesTests extends FunSuite {

  def assertContains(expected: FBFrom, actual: Option[FBFrom]): Unit = {
    actual match {
      case Some(from) =>
        assert(from.userId == expected.userId)
        assert(from.userName == expected.userName)
      case None =>
        fail("From is None.")
    }
  }

  def assertEqualsOption[T](expectedOpt: Option[T], actualOpt: Option[T]): Unit = expectedOpt match {
    case Some(expected) =>
      assert(actualOpt.contains(expected))
    case None =>
      assert(actualOpt.isEmpty)
  }

  def assertEqualsNoQuestion(expected: Game, actual: Game): Unit = {

    def assertEquals(expectedPlayerMoves: List[Move], actualPlayerMoves: List[Move]): Unit = {
      assert(expectedPlayerMoves.length == actualPlayerMoves.length)
      expectedPlayerMoves.zip(actualPlayerMoves).foreach {
        case (expectedMove, actualMove) =>
          assert(expectedMove.column == actualMove.column)
          assert(expectedMove.row == actualMove.row)
      }
    }

    assert(expected._id == actual._id)
    assert(expected.creationTime == actual.creationTime)
    assert(expected.player1 == actual.player1)
    assert(expected.player2 == actual.player2)
    assert(expected.player1Scores == actual.player1Scores)
    assert(expected.player2Scores == actual.player2Scores)
    assert(expected.playerTurn == actual.playerTurn)
    assert(expected.status == actual.status)
    assert(expected.wonBy == actual.wonBy)

    assert(expected.boardState.length == actual.boardState.length)
    expected.boardState.zip(actual.boardState).foreach {
      case (expectedScores, actualScores) =>
        assert(expectedScores.length == actualScores.length)
        expectedScores.zip(actualScores).foreach {
          case (expectedScore, actualScore) =>
            assert(expectedScore.player == actualScore.player)
            assert(expectedScore.score == actualScore.score)
        }
    }

    assertEquals(expected.player1AvailableMoves, actual.player1AvailableMoves)
    assertEquals(expected.player2AvailableMoves, actual.player2AvailableMoves)

    assertEqualsNoQuestion(expected.player1Board, actual.player1Board)
    assertEqualsNoQuestion(expected.player2Board, actual.player2Board)
  }

  def assertEqualsNoQuestion(expected: Board, actual: Board): Unit = {
    assert(expected._id == actual._id)
    assert(expected.userId == actual.userId)
    assert(expected.tiles.length == actual.tiles.length)

    expected.tiles.zip(actual.tiles).foreach {
      case (expectedTile, actualTile) =>
        assertEqualsNoQuestion(expectedTile, actualTile)
    }
  }

  def assertEqualsNoQuestion(expected: Tile, actual: Tile): Unit = {
    assert(expected.`type` == actual.`type`)
    assert(expected._id == actual._id)
    assert(actual.question1.isEmpty)
    assert(actual.question2.isEmpty)
    assert(actual.question3.isEmpty)
    assert(expected.score == actual.score)
    assert(expected.answered == actual.answered)
    assert(expected.disabled == actual.disabled)
  }

  test("PageSubject") {
    implicit val formats = DefaultFormats

    val page =
      """{
             "name": "Blood Bowl",
             "pageId": "13590131663",
             "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
             "type": "Page"
           }"""

    val pageSubject = parse(page).extract[PageSubject]

    assert(pageSubject.name == "Blood Bowl")
    assert(pageSubject.pageId == "13590131663")
    assert(pageSubject.`type` == SubjectType.PageSubject)
    assert(pageSubject.photoUrl.contains("https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_" +
      "114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0"))
  }

  test("TextPostSubject") {
    implicit val formats = DefaultFormats

    val textPost =
      """{
            "text": "I will post some weird things in the future...",
            "type": "TextPost"
          }"""

    val postSubject = parse(textPost).extract[TextPostSubject]

    assert(postSubject.text == "I will post some weird things in the future...")
    assert(postSubject.`type` == SubjectType.TextPost)
    assert(postSubject.from.isEmpty)
  }

  test("ImagePostSubject") {
    implicit val formats = DefaultFormats

    val imagePost =
      """{
            "text": "You changed your profile picture.",
            "imageUrl": "https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/p180x540/396092_10151324342204968_433100355_n.jpg?oh=107c5dc045e438b5273a14d568c934c5&oe=55F0453B",
            "facebookImageUrl": "https://www.facebook.com/photo.php?fbid=10151324342204968&set=a.499536429967.288132.656209967&type=1",
            "type": "ImagePost"
          }"""

    val imagePostSubject = parse(imagePost).extract[ImagePostSubject]

    assert(imagePostSubject.text == "You changed your profile picture.")
    assert(imagePostSubject.imageUrl.contains("https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/p180x540/" +
      "396092_10151324342204968_433100355_n.jpg?oh=107c5dc045e438b5273a14d568c934c5&oe=55F0453B"))
    assert(imagePostSubject.facebookImageUrl.contains("https://www.facebook.com/photo.php?fbid=10151324342204968&set=a.499536429967.288132.656209967&type=1"))
    assert(imagePostSubject.`type` == SubjectType.ImagePost)
    assert(imagePostSubject.from.isEmpty)
  }

  test("VideoPostSubject") {
    implicit val formats = DefaultFormats

    val videoPost =
      """{
            "text": "Roger Küng shared a link.",
            "thumbnailUrl": "https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1",
            "url": "http://www.youtube.com/watch?v=fzMhh8zhTiY",
            "type": "VideoPost",
            "from": {
                "userId": "656209967",
                "userName": "Roger Küng"
            }
          }"""

    val videoPostSubject = parse(videoPost).extract[VideoPostSubject]

    assert(videoPostSubject.text == "Roger Küng shared a link.")
    assert(videoPostSubject.thumbnailUrl.contains("https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720" +
      "&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1"))
    assert(videoPostSubject.url.contains("http://www.youtube.com/watch?v=fzMhh8zhTiY"))
    assert(videoPostSubject.`type` == SubjectType.VideoPost)

    assertContains(FBFrom("656209967", "Roger Küng"), videoPostSubject.from)
  }

  test("LinkPostSubject") {
    implicit val formats = DefaultFormats

    val linkPost =
      """{
            "text": "I guess the U.S. Healthcare state needs some readjustment",
            "thumbnailUrl": "https://external.xx.fbcdn.net/safe_image.php?d=AQBP8YvTVf_VKUMb&w=720&h=720&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg&cfs=1",
            "url": "http://redditpics.fpapps.com/?thingid=t3_3dngld&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg",
            "type": "LinkPost",
            "from": {
                "userId": "656209967",
                "userName": "Roger Küng"
            }
         }"""

    val linkPostSubject = parse(linkPost).extract[LinkPostSubject]

    assert(linkPostSubject.text == "I guess the U.S. Healthcare state needs some readjustment")
    assert(linkPostSubject.thumbnailUrl.contains("https://external.xx.fbcdn.net/safe_image.php?d=AQBP8YvTVf_VKUMb&w=720&" +
      "h=720&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg&cfs=1"))
    assert(linkPostSubject.url.contains("http://redditpics.fpapps.com/?thingid=t3_3dngld&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg"))
    assert(linkPostSubject.`type` == SubjectType.LinkPost)
    assertContains(FBFrom("656209967", "Roger Küng"), linkPostSubject.from)
  }

  test("CommentSubject") {
    implicit val formats = DefaultFormats

    val comment =
      """{
            "comment": "I just noticed that. How is it scientific if it's not your usual and natural behavior ? ;)",
            "post": {
                "text": "I will post some weird things in the future, but its for science so be prepared to see some abnormal activity...",
                "type": "TextPost"
            },
            "type": "Comment"
          }"""

    val commentSubject = parse(comment).extract[CommentSubject]

    assert(commentSubject.comment == "I just noticed that. How is it scientific if it's not your usual and natural behavior ? ;)")
    assert(commentSubject.`type` == SubjectType.CommentSubject)
    assert(commentSubject.post.isEmpty)
  }


  test("TimelineQuestion") {
    implicit val formats = DefaultFormats

    val question =
      """ {
            "subject": {},
            "min": "2015-06-03T11:41:09+0000",
            "max": "2015-06-07T11:41:09+0000",
            "default": "2015-06-03T11:41:09+0000",
            "unit": "Day",
            "step": 1,
            "threshold": 0,
            "answer": "2015-06-07T11:41:09+0000",
            "type": "TLWhenDidYouShareThisPost",
            "kind": "Timeline"
          }"""

    val parsedQuestion = parse(question).extract[TimelineQuestion]

    assert(parsedQuestion.subject.isEmpty)
    assert(parsedQuestion.min == "2015-06-03T11:41:09+0000")
    assert(parsedQuestion.max == "2015-06-07T11:41:09+0000")
    assert(parsedQuestion.default == "2015-06-03T11:41:09+0000")
    assert(parsedQuestion.unit == "Day")
    assert(parsedQuestion.step == 1)
    assert(parsedQuestion.threshold == 0)
    assert(parsedQuestion.answer == "2015-06-07T11:41:09+0000")
    assert(parsedQuestion.kind == QuestionKind.Timeline)
    assert(parsedQuestion.`type` == "TLWhenDidYouShareThisPost")
  }

  test("MultipleChoiceQuestion") {
    implicit val formats = DefaultFormats

    val question =
      """ {
            "subject": {},
            "choices": [
            {
              "text": "Maria Maria",
              "imageUrl": null,
              "fbId": "10152584486929069",
              "pageId": null
            },
            {
              "text": "Zelal Al-Shemmery",
              "imageUrl": null,
              "fbId": "768569646537959",
              "pageId": null
            },
            {
              "text": "Christian M. Schmid",
              "imageUrl": null,
              "fbId": "10201396264188446",
              "pageId": null
            },
            {
              "text": "Michalina Pacholska",
              "imageUrl": null,
              "fbId": "714035445332109",
              "pageId": null
            }],
            "answer": 1,
            "type": "MCWhoLikedYourPost",
            "kind": "MultipleChoice"
          }"""

    assert(parse(question).extract[MultipleChoiceQuestion] == MultipleChoiceQuestion(QuestionKind.MultipleChoice, "MCWhoLikedYourPost",
      None, List(Possibility("Maria Maria", None, Some("10152584486929069"), None), Possibility("Zelal Al-Shemmery", None, Some("768569646537959"), None)
        , Possibility("Christian M. Schmid", None, Some("10201396264188446"), None),
        Possibility("Michalina Pacholska", None, Some("714035445332109"), None)), 1))

    val parsedQuestion = parse(question).extract[MultipleChoiceQuestion]

    assert(parsedQuestion.kind == QuestionKind.MultipleChoice)
    assert(parsedQuestion.`type` == "MCWhoLikedYourPost")
    assert(parsedQuestion.subject.isEmpty)

    val expectedChoices = List(Possibility("Maria Maria", None, Some("10152584486929069"), None),
      Possibility("Zelal Al-Shemmery", None, Some("768569646537959"), None),
      Possibility("Christian M. Schmid", None, Some("10201396264188446"), None),
      Possibility("Michalina Pacholska", None, Some("714035445332109"), None))

    assert(parsedQuestion.choices.length == expectedChoices.length)

    parsedQuestion.choices.zip(expectedChoices).foreach {
      case (expected, actual) =>
        assert(expected.text == actual.text)
        assertEqualsOption(expected.fbId, actual.fbId)
        assertEqualsOption(expected.imageUrl, actual.imageUrl)
        assertEqualsOption(expected.pageId, actual.pageId)
    }
  }

  test("GeolocationQuestion") {
    implicit val formats = DefaultFormats

    val question =
      """{
                               "subject": {},
                               "range": 0.02612831795,
                               "defaultLocation": {
                                 "latitude": 46.54730608686859,
                                 "longitude": 6.57538616738275
                               },
                               "answer": {
                                 "latitude": 46.519681242464,
                                 "longitude": 6.5717116820427
                               },
                               "type": "GeoWhatCoordinatesWereYouAt",
                               "kind": "Geolocation"
                             }"""

    assert(parse(question).extract[GeolocationQuestion] == GeolocationQuestion(None, 0.02612831795,
      Location(46.54730608686859, 6.57538616738275), Location(46.519681242464, 6.5717116820427),
      "GeoWhatCoordinatesWereYouAt", QuestionKind.Geolocation))

    val parsedQuestion = parse(question).extract[GeolocationQuestion]

    assert(parsedQuestion.subject.isEmpty)
    assert(parsedQuestion.range == 0.02612831795)
    assert(parsedQuestion.defaultLocation.latitude == 46.54730608686859)
    assert(parsedQuestion.defaultLocation.longitude == 6.57538616738275)
    assert(parsedQuestion.answer.latitude == 46.519681242464)
    assert(parsedQuestion.answer.longitude == 6.5717116820427)
    assert(parsedQuestion.`type` == "GeoWhatCoordinatesWereYouAt")
    assert(parsedQuestion.kind == QuestionKind.Geolocation)
  }

  test("Tile") {
    implicit val formats = DefaultFormats

    val tile =
      """{
      "_id": "87c565b56f7fc92ff8617c2b",
      "type": "Timeline",
      "question1": {},
      "question2": {},
      "question3": {},
      "score": 0,
      "answered": false,
      "disabled": false
    }"""

    val parsedTile = parse(tile).extract[Tile]
    val expectedTile = Tile("Timeline", "87c565b56f7fc92ff8617c2b", None, None, None, 0, answered = false, disabled = false)

    assertEqualsNoQuestion(expectedTile, parsedTile)
  }

  test("Board") {
    implicit val formats = DefaultFormats

    val board =
      """{
                      "userId": "MmYXQ5EKSgdjzP3uJ",
                      "tiles": [
                        {
                          "_id": "79c2378123949e4386631412",
                          "type": "MultipleChoice",
                          "question1": {},
                          "question2": {},
                          "question3": {},
                          "score": 0,
                          "answered": false,
                          "disabled": false
                        },
                        {
                          "_id": "9119c06edf2e473772ea37a2",
                          "type": "Order",
                          "question1": {},
                          "question2": {},
                          "question3": {},
                          "score": 0,
                          "answered": false,
                          "disabled": false
                        },
                        {
                          "_id": "b7893b57c6ba4350fb3d977a",
                          "type": "Order",
                          "question1": {},
                          "question2": {},
                          "question3": {},
                          "score": 0,
                          "answered": false,
                          "disabled": false
                        },
                        {

                         "_id": "6cb42b812a48c42cc4b0024c",
                          "type": "MultipleChoice",
                          "question1": {},
                          "question2": {},
                          "question3": {},
                          "score": 0,
                          "answered": false,
                          "disabled": false
                        }
                      ],
                      "_id": "wQz5dAXnpDcyvrJud"
                    }"""

    val actualBoard = parse(board).extract[Board]
    val expectedBoard = Board("MmYXQ5EKSgdjzP3uJ",
      List(Tile("MultipleChoice", "79c2378123949e4386631412", None, None, None, 0, answered = false, disabled = false),
        Tile("Order", "9119c06edf2e473772ea37a2", None, None, None, 0, answered = false, disabled = false),
        Tile("Order", "b7893b57c6ba4350fb3d977a", None, None, None, 0, answered = false, disabled = false),
        Tile("MultipleChoice", "6cb42b812a48c42cc4b0024c", None, None, None, 0, answered = false, disabled = false)),
      "wQz5dAXnpDcyvrJud")

    assertEqualsNoQuestion(expectedBoard, actualBoard)
  }

  test("Game") {
    implicit val formats = DefaultFormats

    val game =
      """{
                   "_id": "qS3MNajyD4qd2RYpy",
                   "player1": "MmYXQ5EKSgdjzP3uJ",
                   "player2": "wucQxKHvs5W9Ao9y9",
                   "player1Board": {
                      "userId": "MmYXQ5EKSgdjzP3uJ",
                      "tiles": [


                      {
                        "_id": "9119c06edf2e473772ea37a2",
                        "type": "Order",
                        "question1": {},
                        "question2": {},
                        "question3": {},
                        "score": 0,
                        "answered": false,
                        "disabled": false
                      },
                      {
                        "_id": "b7893b57c6ba4350fb3d977a",
                        "type": "Order",
                        "question1": {},
                        "question2": {},
                        "question3": {},
                        "score": 0,
                        "answered": false,
                        "disabled": false
                      },
                      {
                        "_id": "6cb42b812a48c42cc4b0024c",
                        "type": "MultipleChoice",
                        "question1": {},
                        "question2": {},
                        "question3": {},
                        "score": 0,
                        "answered": false,
                        "disabled": false
                      }
                      ],
                      "_id": "wQz5dAXnpDcyvrJud"
                    },
                   "player2Board": {
                     "userId": "wucQxKHvs5W9Ao9y9",
                     "tiles": [

                       {
                         "_id": "7fe68a7b1edf66b0dae1aaac",
                         "type": "Geolocation",
                         "question1": {},
                         "question2": {},
                         "question3": {},
                         "score": 0,
                         "answered": false,
                         "disabled": false
                       },
                       {
                         "_id": "099a1cec7b072135919f04fb",
                         "type": "MultipleChoice",
                         "question1": {},
                         "question2": {},
                         "question3": {},
                         "score": 0,
                         "answered": false,
                         "disabled": false
                       }
                     ],
                     "_id": "fAYrgj74h9dv9Dhmq"
                   },
                   "status": "ended",
                   "playerTurn": 1,
                   "player1Scores": 3,
                   "player2Scores": 7,
                   "boardState": [
                     [
                       {
                         "player": 1,
                         "score": 0
                       },
                       {
                         "player": 2,
                         "score": 0
                       },
                       {
                         "player": 2,
                         "score": 3
                       }
                     ],
                     [
                       {
                         "player": 2,
                         "score": 0
                       },
                       {
                         "player": 2,
                         "score": 3
                       },
                       {
                         "player": 1,
                         "score": 0
                       }
                     ],
                     [
                       {
                         "player": 2,
                         "score": 1
                       },
                       {
                         "player": 1,
                         "score": 1
                       },
                       {
                         "player": 0,
                         "score": 0
                       }
                     ]
                   ],
                   "player1AvailableMoves": [
                     {
                       "row": 0,
                       "column": 1
                     },
                     {
                       "row": 1,
                       "column": 0
                     },
                     {
                       "row": 2,
                       "column": 0
                     },
                     {
                       "row": 2,
                       "column": 2
                     }
                   ],
                   "player2AvailableMoves": [
                     {
                       "row": 0,
                       "column": 0
                     },
                     {
                       "row": 1,
                       "column": 2
                     },
                     {
                       "row": 2,
                       "column": 1
                     },
                     {
                       "row": 2,
                       "column": 2
                     }
                   ],
                   "wonBy": 2,
                   "creationTime": 1456062881638
                 }"""

    val expectedGame = Game("qS3MNajyD4qd2RYpy", "MmYXQ5EKSgdjzP3uJ", "wucQxKHvs5W9Ao9y9",
      Board("MmYXQ5EKSgdjzP3uJ", List(Tile("Order", "9119c06edf2e473772ea37a2", None, None, None, 0, answered = false, disabled = false),
        Tile("Order", "b7893b57c6ba4350fb3d977a", None, None, None, 0, answered = false, disabled = false),
        Tile("MultipleChoice", "6cb42b812a48c42cc4b0024c", None, None, None, 0, answered = false, disabled = false)), "wQz5dAXnpDcyvrJud"),
      Board("wucQxKHvs5W9Ao9y9", List(Tile("Geolocation", "7fe68a7b1edf66b0dae1aaac", None, None, None, 0, answered = false, disabled = false),
        Tile("MultipleChoice", "099a1cec7b072135919f04fb", None, None, None, 0, answered = false, disabled = false)), "fAYrgj74h9dv9Dhmq"),
      "ended", 1, 3, 7, List(List(Score(1, 0), Score(2, 0), Score(2, 3)), List(Score(2, 0), Score(2, 3), Score(1, 0)),
        List(Score(2, 1), Score(1, 1), Score(0, 0))), List(Move(0, 1), Move(1, 0), Move(2, 0), Move(2, 2)), List(Move(0, 0),
        Move(1, 2), Move(2, 1), Move(2, 2)), 2, 1456062881638L)

    val actualGame = parse(game).extract[Game]

    assertEqualsNoQuestion(expectedGame, actualGame)
  }

  test("json from file") {
    implicit val formats = DefaultFormats + new QuestionSerializer + new SubjectSerializer
    val stream: InputStream = getClass.getResourceAsStream("/boards.json")
    val lines = scala.io.Source.fromInputStream(stream).getLines.mkString("\n")
    val json = parse(lines)
    assert(json.extract[Game].isInstanceOf[Game])
  }

}
