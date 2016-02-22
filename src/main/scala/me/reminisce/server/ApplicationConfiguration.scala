package me.reminisce.server

import scala.util.Properties._


object ApplicationConfiguration {


  val appMode = "DEV"

  val hostName = "localhost"
  val serverPort = 7777

  val mongoHost = envOrElse("MONGODB_HOST", hostName)
  val mongodbName = envOrElse("REMINISCE_ STATS_MONGO_DB", "reminisce_stats")

}
