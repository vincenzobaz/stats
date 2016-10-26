package me.reminisce.server.jsonserializer

import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import me.reminisce.server.{QuestionSerializer, SubjectSerializer}

/**
  * Defines the json serialization formats
  */
trait StatsFormatter {
  implicit lazy val json4sFormats: Formats = DefaultFormats + new QuestionSerializer() + new SubjectSerializer()  ++ JodaTimeSerializers.all
}

