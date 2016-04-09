package me.reminisce.server.jsonserializer

import org.json4s.ext.{EnumNameSerializer, JodaTimeSerializers}
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport

/**
  * Defines the json serialization formats
  */
trait StatsFormatter {
  implicit lazy val json4sFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all
}

