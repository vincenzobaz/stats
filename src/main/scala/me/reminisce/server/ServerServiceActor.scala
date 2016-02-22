package me.reminisce.server

import akka.actor.{Actor, ActorLogging}
import reactivemongo.api.{MongoConnection, MongoDriver}

/**
  * The actor handling the http request in this application. It handles the requests according to the
  * routes defined in [[me.reminisce.server.StatsService]]
  */
class ServerServiceActor extends Actor with StatsServiceActor with ActorLogging {
  override def actorRefFactory = context

  override def receive = runRoute(statsRoutes)

  val driver = new MongoDriver
  val mongoHost = ApplicationConfiguration.mongoHost
  val mongodbName = ApplicationConfiguration.mongodbName
  val connection: MongoConnection = driver.connection(List(mongoHost))


  /**
    * Cascades the shutdown to the mongo driver.
    */
  override def postStop(): Unit = {
    connection.close()
    driver.system.shutdown()
    driver.close()
  }

}
