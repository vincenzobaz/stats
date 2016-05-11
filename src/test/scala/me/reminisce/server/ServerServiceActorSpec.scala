package me.reminisce.server

import java.util.concurrent.TimeUnit

import akka.testkit.TestActorRef
import me.reminisce.database.DatabaseTester
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats}
import org.scalatest.DoNotDiscover
import spray.client.pipelining._
import spray.http._

import scala.concurrent.duration.Duration


@DoNotDiscover
class ServerServiceActorSpec extends DatabaseTester("ServerServiceActorSpec") {

  case class SimpleMessageFormat(message: String)

  val testService = TestActorRef[ServerServiceActor]

  implicit def json4sFormats: Formats = DefaultFormats
  
  val statsTest = Stats(
    "userID123", 
    Some(CountWinnerGame(2, 3)), 
    Some(AverageScore(2.3)), 
    Some(CountCorrectQuestion(2,8)), 
    Some(DateTime.now), 
    Some("nkj261ma"))

  "ServerServiceActor" must {

    "try to insert a Game." in {
      val url = "/insertEntity"
      val postRequest = new HttpPost(url)
      post.addHeader("content-type","application/json; charset=UTF-8")
      val nameValuePairs = new ArrayList[NameValuePair]()
      nameValuePairs.add(new BasicNameValuePair("JSON", spockAsJson));
      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
     
    }

   

    "try to insert." in {

    }

    "try to fetch." in {
      val gethRequest = new HttpRequest(uri = "/getStatistics?userID=user1")
      assert(gethRequest.method == HttpMethods.GET)
      testService ! gethRequest
      val responseOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      responseOpt match {
        case Some(response) =>
          assert(response.isInstanceOf[HttpResponse])

          val httpResponse = response.asInstanceOf[HttpResponse]
          assert(httpResponse.status == StatusCodes.Unauthorized)
          val json = parse(httpResponse.entity.data.asString)
          //TODO: json deserializer for Stats
          val message = json.extract[Stats]
          assert(message.message == "The specified token is invalid.")
        case None =>
          fail("Response is not defined.")
      }
    } 

    "try to generate a gameboard." in {
      val gameboardRequest = Get(s"/gameboard?user_id=XXX&access_token=XXX&strategy=random")

      testService ! gameboardRequest

      val gameboardAnswerOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      gameboardAnswerOpt match {
        case Some(gameboardAnswer) =>
          assert(gameboardAnswer.isInstanceOf[HttpResponse])
          val gameboardHttpResponse = gameboardAnswer.asInstanceOf[HttpResponse]
          assert(gameboardHttpResponse.status == StatusCodes.InternalServerError)
        case None =>
          fail("Did not receive an answer.")
      }
    }

    "delete user." in {
      val deleteRequest = Delete(s"/removeUser?user_id=XXX")
      testService ! deleteRequest

      val feedbackOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      val status = extractStatus(feedbackOpt)
      status match {
        case StatusCodes.OK =>
        case StatusCodes.Forbidden =>
          fail("This should not be forbidden.")
        case StatusCodes.InternalServerError =>
          cancel("This test is canceled due to a 500 error that may arise when testing in a docker container," +
            " if you are not running in such a container, you should NOT ignore this.")
        case any => fail(s"Unexpected status: $any.")
      }
    }

    "drop database." in {
      val deleteRequest = Delete(s"/dropDatabase")
      testService ! deleteRequest

      val feedbackOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      val status = extractStatus(feedbackOpt)
      status match {
        case StatusCodes.OK => assert(ApplicationConfiguration.appMode == "DEV")
        case StatusCodes.Forbidden => assert(ApplicationConfiguration.appMode != "DEV")
        case StatusCodes.InternalServerError =>
          cancel("This test is canceled due to a 500 error when testing in a docker container, if you are not" +
            "running in a container, you should NOT ignore this.")
        case any => fail(s"Unexpected status: $any.")
      }
    }

    "get info." in {
      val deleteRequest = Get(s"/info")
      testService ! deleteRequest

      val feedbackOpt = Option(receiveOne(Duration(10, TimeUnit.SECONDS)))
      assert(extractStatus(feedbackOpt) == StatusCodes.OK)
    }
  }

  private def extractStatus(feedbackOpt: Option[AnyRef]): StatusCode = {
    feedbackOpt match {
      case Some(feedback) =>
        assert(feedback.isInstanceOf[HttpResponse])
        val feedbackHttpResponse = feedback.asInstanceOf[HttpResponse]
        feedbackHttpResponse.status
      case None =>
        fail("Did not receive feedback.")
    }
  }
}
