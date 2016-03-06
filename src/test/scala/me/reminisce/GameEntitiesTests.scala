package me.reminisce

import org.json4s.native.Serialization.read
import org.scalatest.FunSuite

import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import me.reminisce.server.GameEntities._
/**
  * Created by sandra on 04/03/16.
  */

class GameEntitiesTests extends FunSuite{


  val json = Source.fromFile("/Users/sandra/Documents/EPFL/BachelorProject/boards.json").getLines.mkString("\n")
  val content = parse(json)

  import org.json4s.DefaultFormats
  implicit val formats = DefaultFormats

 //val result2 = (content ).extract[Game]
  //print(content)
  //print(content.extract[Game])

 /* case class User(name: String, emails: List[String])
  val string = """{"users":
    [
    {"name": "Foo", "emails": ["Foo@gmail.com", "foo2@gmail.com"]},
    {"name": "Bar", "emails": ["Bar@gmail.com", "bar@gmail.com"]}
    ]
  }""""
  print(parse(string).extract[List[User]])*/
  val board = content \\ "player2Board" \\ "tiles" \\ "_id"
  print(board)
  //print((content \ "player2Board").extract[Board] )
  //print( (parse(json) \ "choices").extract[List[MultipleChoiceQuestion]])
  //print(read[Game](json))
}
