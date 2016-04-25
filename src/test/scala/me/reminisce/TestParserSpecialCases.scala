package me.reminisce

import com.fasterxml.jackson.core.JsonParseException
import me.reminisce.server.GameEntities._
import me.reminisce.server.{SubjectSerializer, QuestionSerializer}
import org.scalatest.FunSuite
import org.json4s._
import org.json4s.jackson.JsonMethods._

class TestParserSpecialCases extends FunSuite{

  test("chinese characters"){
    implicit val formats = DefaultFormats

    val videoPost =
      """{
            "text": "Roger Küng 分享了一個鏈接。",
            "thumbnailUrl": "https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1",
            "url": "http://www.youtube.com/watch?v=fzMhh8zhTiY",
            "type": "VideoPost",
            "from": {
                "userId": "656209967",
                "userName": "Roger Küng"
            }
          }"""

    assert(parse(videoPost).extract[VideoPostSubject] == VideoPostSubject("Roger Küng 分享了一個鏈接。",
      Some("https://external.xx.fbcdn.net/safe_image.php?d=AQBgK6vGG5nYP2hm&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FfzMhh8zhTiY%2F0.jpg&cfs=1"),
      Some("http://www.youtube.com/watch?v=fzMhh8zhTiY"), SubjectType.VideoPost,
      Some(FBFrom("656209967", "Roger Küng"))))
  }

  test("special characters"){
    implicit val formats = DefaultFormats

    val textPost =
      """{
            "text": "I will post some weird things in the future...☺$¤paste†‡¾",
            "type": "TextPost"
          }"""

    assert(parse(textPost).extract[TextPostSubject] == TextPostSubject("I will post some weird things in the future...\u263A$¤paste†‡¾"
      , SubjectType.TextPost, None))
  }

  test("missing field") {
    implicit val formats = DefaultFormats

    val question =
      """{
                               "subject": {},
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

    intercept[MappingException] {
      parse(question).extract[GeolocationQuestion]
    }
  }

    test("chinese character instead of double value") {
      implicit val formats = DefaultFormats

      val question =
        """{
                               "subject": {},
                               "range": 分享了一個鏈接,
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


      intercept[JsonParseException] {
        parse(question).extract[GeolocationQuestion]
      }
  }

}
