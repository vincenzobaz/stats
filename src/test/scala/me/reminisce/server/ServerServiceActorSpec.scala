package me.reminisce.server

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import akka.testkit.TestActorRef
import me.reminisce.database.DatabaseTester
import me.reminisce.statistics.StatisticEntities._
import me.reminisce.server.GameEntities._
import org.scalatest.DoNotDiscover
import spray.client.pipelining._
import spray.http._
import HttpMethods._
import HttpHeaders._
import ContentTypes._
import com.github.nscala_time.time.Imports._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.JDouble
import org.json4s.JInt
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JField
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s._
import org.json4s.jackson.JsonMethods._

//@DoNotDiscover
class ServerServiceActorSpec extends DatabaseTester("ServerServiceActorSpec") {

  case class SimpleMessageFormat(message: String)

  val testService = TestActorRef[ServerServiceActor]

  implicit def json4sFormats: Formats = DefaultFormats
  
  val statsTest = Stats(
    "userID123", 
    Some(CountWinnerGame(2, 3)), 
    Some(AverageScore(2.3)), 
    Some(CountCorrectQuestion(2,8)), 
    Some(com.github.nscala_time.time.Imports.DateTime.now), 
    Some("nkj261ma"))

  "ServerServiceActor" must {

    "try to insert a Game." in {

      val url = "/insertEntity"
      val randomID = java.util.UUID.randomUUID
      val randomUser1 = java.util.UUID.randomUUID
      val randomUser2 = java.util.UUID.randomUUID
      val gameJson : String =
              s"""{
                   "_id": $randomID,
                   "player1": $randomUser1,
                   "player2": $randomUser2,
                   "player1Board": {
                      "userId": $randomUser1,
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
                     "userId": $randomUser2,
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

      val postRequest = new HttpRequest(method = HttpMethods.POST, entity = gameJson)
      assert(postRequest.method == HttpMethods.POST)
      
      testService ! postRequest.withHeaders(List(`Content-Type`(`application/json`)))
      
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      
      responseOpt match {
        case Some(response) =>
          println(s"   Message received :    $response")
          assert(response.isInstanceOf[HttpResponse])

          val httpResponse = response.asInstanceOf[HttpResponse]
          assert(httpResponse.status == StatusCodes.Unauthorized)
          val json = parse(httpResponse.entity.data.asString)
          println(json)
          //TODO: json deserializer for Stats
          //val message = json.extract[Stats]
         // assert(message.message == "The specified token is invalid.")
        case None =>
          println("Unknown Message reveiced")
          fail("Response is not defined.")
      }

    }

    "try to retrieve Stats." in {
      val getRequest = new HttpRequest(uri = "/getStatistics?userID=user1")
      assert(getRequest.method == HttpMethods.GET)
      testService ! getRequest
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])

          val httpResponse = response.asInstanceOf[HttpResponse]
          assert(httpResponse.status == StatusCodes.Unauthorized)
          val json = parse(httpResponse.entity.data.asString)
          println(json)
          //TODO: json deserializer for Stats
          //val message = json.extract[Stats]
          //assert(message.message == "The specified token is invalid.")

        case None =>
          fail("Response is not defined.")
      }
    } 
  }
}
