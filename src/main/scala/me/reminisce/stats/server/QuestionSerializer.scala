package me.reminisce.stats.server

import me.reminisce.stats.server.GameEntities.{GameQuestion, QuestionKind}
import org.json4s.JsonAST.{JField, JObject, JString}
import org.json4s._

class QuestionSerializer extends CustomSerializer[GameQuestion](implicit formats => ( {
  case question: JObject =>
    val kind = (question \ "kind").extract[String]
    val tpe = (question \ "type").extract[String]
    val correct = (question \ "correct").extractOpt[Boolean]

    GameQuestion(QuestionKind.withName(kind), tpe, correct)
}, {
  case GameQuestion(kind, tpe, correctOpt) =>
    correctOpt match {
      case Some(correct) =>
        JObject(List(JField("kind", JString("Timeline")), JField("type", JString(tpe)), JField("correct", JBool(correct))))
      case None =>
        JObject(List(JField("kind", JString("Timeline")), JField("type", JString(tpe))))
    }
}))