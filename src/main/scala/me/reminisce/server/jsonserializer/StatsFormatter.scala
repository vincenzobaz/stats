package me.reminisce.server.jsonserializer

import me.reminisce.server.QuestionSerializer
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}

/**
  * Defines the json serialization formats
  */
trait StatsFormatter {
  implicit lazy val json4sFormats: Formats = DefaultFormats + new QuestionSerializer ++ JodaTimeSerializers.all
}