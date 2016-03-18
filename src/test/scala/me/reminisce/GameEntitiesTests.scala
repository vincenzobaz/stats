package me.reminisce

import java.io.InputStream
import me.reminisce.server.GameEntities._
import org.json4s.JDouble
import org.json4s.JInt
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JField
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.scalatest.FunSuite
import org.json4s._
import org.json4s.jackson.JsonMethods._

class GameEntitiesTests extends FunSuite {

  test("PageSubject") {
    implicit val formats = DefaultFormats

    val pageSubject =
      """{
             "name": "Blood Bowl",
             "pageId": "13590131663",
             "photoUrl": "https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0",
             "type": "Page"
           }"""

    assert(parse(pageSubject).extract[PageSubject] == PageSubject("Blood Bowl",
      "13590131663",
      Some("https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0"),
      SubjectType.PageSubject)
    )
  }

  test("TextPostSubject") {
    implicit val formats = DefaultFormats

    val textPost =
      """{
            "text": "I will post some weird things in the future...",
            "type": "TextPost"
          }"""

    assert(parse(textPost).extract[TextPostSubject] == TextPostSubject("I will post some weird things in the future..."
      , SubjectType.TextPost, None))
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

    assert(parse(imagePost).extract[ImagePostSubject] == ImagePostSubject("You changed your profile picture.",
      Some("https://scontent.xx.fbcdn.net/hphotos-xfa1/v/t1.0-9/p180x540/396092_10151324342204968_433100355_n.jpg?oh=107c5dc045e438b5273a14d568c934c5&oe=55F0453B"),
      Some("https://www.facebook.com/photo.php?fbid=10151324342204968&set=a.499536429967.288132.656209967&type=1"), SubjectType.ImagePost, None))
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

    assert(parse(videoPost).extract[VideoPostSubject] == VideoPostSubject("Roger Küng shared a link.",
      Some("https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1"),
      Some("http://www.youtube.com/watch?v=fzMhh8zhTiY"), SubjectType.VideoPost,
      Some(FBFrom("656209967", "Roger Küng"))))
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

    assert(parse(linkPost).extract[LinkPostSubject] == LinkPostSubject("I guess the U.S. Healthcare state needs some readjustment",
      Some("https://external.xx.fbcdn.net/safe_image.php?d=AQBP8YvTVf_VKUMb&w=720&h=720&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg&cfs=1"),
      Some("http://redditpics.fpapps.com/?thingid=t3_3dngld&url=http%3A%2F%2Fi.imgur.com%2FBbQgU8a.jpg"), SubjectType.LinkPost,
      Some(FBFrom("656209967", "Roger Küng"))))
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

    assert(parse(comment).extract[CommentSubject] == CommentSubject("I just noticed that. How is it scientific if it's not your usual and natural behavior ? ;)",
      None, SubjectType.CommentSubject))
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

    assert(parse(question).extract[TimelineQuestion] == TimelineQuestion(None,
      "2015-06-03T11:41:09+0000", "2015-06-07T11:41:09+0000", "2015-06-03T11:41:09+0000",
      "Day", 1, 0, "2015-06-07T11:41:09+0000", QuestionKind.Timeline, "TLWhenDidYouShareThisPost"))
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

    assert(parse(tile).extract[Tile] == Tile("Timeline", "87c565b56f7fc92ff8617c2b", None, None, None, 0, false, false))
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

    assert(parse(board).extract[Board] == Board("MmYXQ5EKSgdjzP3uJ", List(Tile("MultipleChoice", "79c2378123949e4386631412", None, None, None, 0, false, false),
      Tile("Order", "9119c06edf2e473772ea37a2", None, None, None, 0, false, false),
      Tile("Order", "b7893b57c6ba4350fb3d977a", None, None, None, 0, false, false),
      Tile("MultipleChoice", "6cb42b812a48c42cc4b0024c", None, None, None, 0, false, false)),
      "wQz5dAXnpDcyvrJud"))
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

    assert(parse(game).extract[Game] == Game("qS3MNajyD4qd2RYpy", "MmYXQ5EKSgdjzP3uJ", "wucQxKHvs5W9Ao9y9",
      Board("MmYXQ5EKSgdjzP3uJ", List(Tile("Order", "9119c06edf2e473772ea37a2", None, None, None, 0, false, false),
        Tile("Order", "b7893b57c6ba4350fb3d977a", None, None, None, 0, false, false),
        Tile("MultipleChoice", "6cb42b812a48c42cc4b0024c", None, None, None, 0, false, false)), "wQz5dAXnpDcyvrJud"),
      Board("wucQxKHvs5W9Ao9y9", List(Tile("Geolocation", "7fe68a7b1edf66b0dae1aaac", None, None, None, 0, false, false),
        Tile("MultipleChoice", "099a1cec7b072135919f04fb", None, None, None, 0, false, false)), "fAYrgj74h9dv9Dhmq"),
      "ended", 1, 3, 7, List(List(Score(1, 0), Score(2, 0), Score(2, 3)), List(Score(2, 0), Score(2, 3), Score(1, 0)),
        List(Score(2, 1), Score(1, 1), Score(0, 0))), List(Move(0, 1), Move(1, 0), Move(2, 0), Move(2, 2)), List(Move(0, 0),
        Move(1, 2), Move(2, 1), Move(2, 2)), 2, 68968294))

  }

  test("json from file") {
    implicit val formats = DefaultFormats + new QuestionSerializer + new SubjectSerializer
    val stream: InputStream = getClass.getResourceAsStream("/boards.json")
    val lines = scala.io.Source.fromInputStream(stream).getLines.mkString("\n")
    val json = parse(lines)
    assert(json.extract[Game].isInstanceOf[Game])
  }

  class QuestionSerializer extends CustomSerializer[GameQuestion](implicit formats => ( {

    case JObject(List(JField("choices", JArray(choices)), JField("items", JArray(items)), JField("answer", JArray(answer)),
    JField("type", JString(tpe)), JField("kind", JString(kind)))) =>
      OrderQuestion(QuestionKind.Order, tpe.toString, choices.map(c => c.extract[SubjectWithId]), items.map(c => c.extract[Item]),
        answer.map(a => a.extract[Int]))

    case JObject(List(JField("subject", subject), JField("min", JString(min)), JField("max", JString(max)), JField("default", JString(default)),
    JField("unit", JString(unit)), JField("step", JInt(step)), JField("threshold", JInt(threshold)), JField("answer", JString(answer)),
    JField("type", JString(tpe)), JField("kind", JString(kind)))) =>
      TimelineQuestion(subject.extractOpt[Subject], min, max, default, unit, step.toInt, threshold.toInt, answer, QuestionKind.Timeline, tpe)

    case JObject(List(JField("subject", subject), JField("choices", JArray(choices)), JField("answer", JInt(answer)),
    JField("type", JString(tpe)), JField("kind", JString(kind)))) =>
      MultipleChoiceQuestion(QuestionKind.MultipleChoice, tpe, subject.extractOpt[Subject], choices.map(c => c.extract[Possibility]), answer.toInt)

    case JObject(List(JField("subject", subject), JField("range", JDouble(range)), JField("defaultLocation", defaultLocation),
    JField("answer", answer), JField("type", JString(tpe)), JField("kind", JString(kind)))) =>
      GeolocationQuestion(subject.extractOpt[Subject], range, defaultLocation.extract[Location], answer.extract[Location], tpe, QuestionKind.Geolocation)

  }, {
    case OrderQuestion(kind, tpe, items, choices, answer) => JObject(List(JField("kind", JString("Order")),
      JField("type", JString(tpe)), JField("items", JString(items.toString)), JField("choices", JString(choices.toString())),
      JField("answer", JArray(answer.map(a => JInt(a))))))

    case MultipleChoiceQuestion(kind, tpe, subject, choices, answer) => JObject(List(JField("subject", JString(subject.toString)),
      JField("choices", JString(choices.toString())), JField("answer", JInt(answer)),
      JField("`type`", JString(tpe)), JField("kind", JString("MultipleChoice"))))

    case GeolocationQuestion(subject, range, defaultLocation, answer, tpe, kind) => JObject(List(JField("subject", JString(subject.toString)),
      JField("range", JDouble(range)), JField("defaultLocation", JString(defaultLocation.toString)),
      JField("answer", JString(answer.toString)), JField("type", JString(tpe)), JField("kind", JString("Geolocation"))))

    case TimelineQuestion(subject, min, max, default, unit, step, threshold, answer, kind, tpe) => JObject(List(JField("subject", JString(subject.toString)),
      JField("min", JString(min)), JField("max", JString(max)), JField("default", JString(default)),
      JField("unit", JString(unit)), JField("step", JInt(step)), JField("threshold", JInt(threshold)), JField("answer", JString(answer)),
      JField("kind", JString("Timeline")), JField("type", JString(tpe))))

  }))

  class SubjectSerializer extends CustomSerializer[Subject](implicit formats => ( {
    case JObject(List(JField("name", JString(name)), JField("pageId", JString(pageId)), JField("photoUrl", JString(photoUrl)),
    JField("type", JString(tpe)))) => PageSubject(name, pageId, Some(photoUrl), SubjectType.PageSubject)

    case JObject(List(JField("comment", JString(comment)), JField("post", post), JField("type", JString(tpe)))) =>
      CommentSubject(comment, Some(post.extractOpt[Subject].asInstanceOf[PostSubject]), SubjectType.CommentSubject)

    case JObject(List(JField("text", JString(text)), JField("type", JString(tpe)), JField("from", from))) =>
      TextPostSubject(text, SubjectType.TextPost, from.extractOpt[FBFrom])

    case JObject(List(JField("text", JString(text)), JField("imageUrl", JString(imageUrl)), JField("facebookImageUrl", JString(facebookImageUrl)),
    JField("type", JString(tpe)), JField("from", from))) =>
      ImagePostSubject(text, Some(imageUrl), Some(facebookImageUrl), SubjectType.ImagePost, from.extractOpt[FBFrom])

    case JObject(List(JField("text", JString(text)), JField("thumbnailUrl", JString(thumbnailUrl)), JField("url", JString(url)),
    JField("type", JString(tpe)), JField("from", from))) =>
      if (tpe.equals("VideoPost"))
        VideoPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.VideoPost, from.extractOpt[FBFrom])
      else
        LinkPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.LinkPost, from.extractOpt[FBFrom])

    case JObject(List(JField("text", JString(text)), JField("thumbnailUrl", JString(thumbnailUrl)), JField("url", JString(url)),
    JField("type", JString(tpe)), JField("from", from))) =>
      LinkPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.LinkPost, from.extractOpt[FBFrom])

  }, {
    case PageSubject(name, pageId, photoUrl, tpe) => JObject(List(JField("name", JString(name)), JField("pageId", JString(pageId)),
      JField("photoUrl", JString(photoUrl.toString)), JField("type", JString(tpe.toString))))

    case CommentSubject(comment, post, tpe) => JObject(List(JField("comment", JString(comment)),
      JField("post", JString(post.toString)), JField("type", JString(tpe.toString))))

    case TextPostSubject(text, tpe, from) => JObject(List(JField("text", JString(text)), JField("type", JString(tpe.toString)),
      JField("from", JString(from.toString))))

    case ImagePostSubject(text, imageUrl, facebookImageUrl, tpe, from) => JObject(List(JField("text", JString(text)),
      JField("imageUrl", JString(imageUrl.toString)), JField("facebookImageUrl", JString(facebookImageUrl.toString)),
      JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))

    case LinkPostSubject(text, thumbnailUrl, url, tpe, from) => JObject(List(JField("text", JString(text)),
      JField("thumbnailUrl", JString(thumbnailUrl.toString)), JField("url", JString(url.toString)),
      JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))

    case VideoPostSubject(text, thumbnailUrl, url, tpe, from) => JObject(List(JField("text", JString(text)),
      JField("thumbnailUrl", JString(thumbnailUrl.toString)), JField("url", JString(url.toString)),
      JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))
  }))

}
