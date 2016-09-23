package me.reminisce.server

import scala.util.Properties._


object ApplicationConfiguration {


  val appMode = "DEV"

  val hostName = "localhost"
  
  // Connect to the local DB
  val dbName = "mockDB"

//  val dbName = "reminisce_stats"

  val serverPort = 7777
  val mongoHost = envOrElse("MONGODB_HOST", hostName)
  val mongodbName = envOrElse("REMINISCE_ STATS_MONGO_DB", dbName)

}
