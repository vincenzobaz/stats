package me.reminisce.server

import java.util.concurrent.TimeUnit

import akka.testkit.TestActorRef
import me.reminisce.database.DatabaseTester
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats, _}
import spray.http.ContentTypes._
import spray.http._

import scala.concurrent.duration.Duration
import scala.language.postfixOps

class ServerServiceActorSpec extends DatabaseTester("ServerServiceActorSpec") {

  val testService = TestActorRef[ServerServiceActor]
  val randomID: String = java.util.UUID.randomUUID.toString
  val randomUser1: String = java.util.UUID.randomUUID.toString
  val randomUser2: String = java.util.UUID.randomUUID.toString
  val urlInsert = "/insertEntity"

  implicit def json4sFormats: Formats = DefaultFormats

  case class SimpleMessageFormat(message: String)

  "ServerServiceActor" must {

    "try to insert a Game." in {

      val gameJson: String = JsonEntity.game(randomID, randomUser1, randomUser2)
      val postRequest = new HttpRequest(
        method = HttpMethods.POST,
        uri = urlInsert,
        entity = HttpEntity(`application/json`, gameJson)
      )

      assert(postRequest.method == HttpMethods.POST)
      testService ! postRequest

      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))

      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])

          val httpResponse = response.asInstanceOf[HttpResponse]
          val json = parse(httpResponse.entity.data.asString)
          json match {
            case JObject(List((k, msg))) =>
              assert(k == "status")
              assert(msg == JString("Done"))
            case _ => fail("Fail to insert")
          }
        case None =>
          fail("Response is not defined.")
      }
    }

    "try to retrieve Stats for an unknown user." in {
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
              assert(msg == JString(s"Statistics not found for NOT${randomUser1}"))
            case _ => fail("Response is not defined.")
          }
        case None =>
          fail("No response")
      }
    }

    "try to retrieve Stats for an existing user." in {
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
    "try to insert a duplicate game" in {
      val gameJson: String = JsonEntity.game(randomID, randomUser1, randomUser2)

      val postRequest = new HttpRequest(
        method = HttpMethods.POST,
        uri = urlInsert,
        entity = HttpEntity(`application/json`, gameJson)
      )
      assert(postRequest.method == HttpMethods.POST)
      testService ! postRequest

      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))

      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])
          val httpResponse = response.asInstanceOf[HttpResponse]
          val json = parse(httpResponse.entity.data.asString)
          json match {
            case JObject(a) =>
              assert(a.head._1 == "status")
              assert(a.head._2 == JString("Aborted"))
            case _ => fail("Fail to insert")
          }
        case None =>
          fail("Response is not defined.")
      }
    }
  }
}
