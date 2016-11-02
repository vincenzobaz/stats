package me.reminisce

import me.reminisce.stats.server.GameEntities._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.FunSuite

class TestParserSpecialCases extends FunSuite {

  test("missing field") {
    implicit val formats = DefaultFormats

    val question =
      """{
         "type": "GeoWhatCoordinatesWereYouAt"
       }"""

    intercept[MappingException] {
      parse(question).extract[GameQuestion]
    }
  }
}