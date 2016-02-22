package me.reminisce.server.jsonserializer

import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}
import org.json4s.{DefaultFormats, Formats}

/**
  * Defines the json serialization formats
  */
trait StatsFormatter {
  implicit lazy val json4sFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all
}
