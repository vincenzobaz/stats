package me.reminisce.server

import me.reminisce.server.GameEntities._
import org.json4s.JDouble
import org.json4s.JInt
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JField
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s._

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
