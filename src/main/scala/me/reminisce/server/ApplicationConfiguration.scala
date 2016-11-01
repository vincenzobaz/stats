package me.reminisce.server

import scala.util.Properties._


object ApplicationConfiguration {

  val hostName = "localhost"
  
	val dbName = "reminisce_stats"

  val serverPort = 7777
  val mongoHost = envOrElse("MONGODB_HOST", hostName)
  val mongodbName = envOrElse("REMINISCE_ STATS_MONGO_DB", dbName)

}
