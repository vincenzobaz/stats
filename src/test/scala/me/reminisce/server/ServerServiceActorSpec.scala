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
import me.reminisce.server.JsonEntity._
import org.scalatest.DoNotDiscover
import spray.client.pipelining._
import spray.http._
import HttpMethods._
import HttpHeaders._
import ContentTypes._
import com.github.nscala_time.time.Imports._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.{JDouble, JInt}
import org.json4s.JsonAST.{JArray, JField, JObject, JString}
import org.json4s._
import org.json4s.jackson.JsonMethods._

//@DoNotDiscover
class ServerServiceActorSpec extends DatabaseTester("ServerServiceActorSpec") {

  case class SimpleMessageFormat(message: String)

  val testService = TestActorRef[ServerServiceActor]

  implicit def json4sFormats: Formats = DefaultFormats
  
  val statsTest = StatResponse("userID123", FrequencyOfPlays())
  val randomID: String = java.util.UUID.randomUUID.toString
  val randomUser1: String = java.util.UUID.randomUUID.toString
  val randomUser2: String = java.util.UUID.randomUUID.toString
  
  "ServerServiceActor" must {

    "try to insert a Game." in {

      val url = "/insertEntity"
      
      val gameJson : String = JsonEntity.game(randomID, randomUser1, randomUser2)

      val postRequest = new HttpRequest(        
                          method = HttpMethods.POST,
                          uri = url, 
                          entity = HttpEntity(`application/json`, gameJson)
                          )
      val post = Post(url, gameJson)
      assert(post.method == HttpMethods.POST)
      testService ! postRequest
      
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      
      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])

          val httpResponse = response.asInstanceOf[HttpResponse]
          val json = parse(httpResponse.entity.data.asString)
          
        case None =>
          fail("Response is not defined.")
      }

    }

    "try to retrieve Stats for an unknown user" in {
      val getRequest = new HttpRequest(uri = s"/stats?userId=NOT${randomUser1}")
      assert(getRequest.method == HttpMethods.GET)
      testService ! getRequest
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])
          val httpResponse = response.asInstanceOf[HttpResponse]
          val json = parse(httpResponse.entity.data.asString)
          json match {
            case JObject(List((k, msg))) =>  
              assert(k == "message")
              assert(msg== JString(s"Statistics not found for NOT${randomUser1}"))
            case _ => fail("Response is not defined.")
          }         
        case None =>
          fail("No response")
      }
    }

    "try to retrieve Stats for an existing user" in {
      val getRequest = new HttpRequest(uri = s"/stats?userId=${randomUser1}")
      assert(getRequest.method == HttpMethods.GET)
      testService ! getRequest
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])
          val httpResponse = response.asInstanceOf[HttpResponse]
          val json = parse(httpResponse.entity.data.asString)

          json match {
            case JObject(a) =>
              assert(a.head._1 == "stats")
            case _ =>
              fail("Response isn't a Stats object")
          }
          //TODO create deserializer for Stats
        case None =>
          fail("Response is not defined.")
      }
    } 
  }
}
