package me.reminisce.server

import akka.actor.{Actor, ActorLogging}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The actor handling the http request in this application. It handles the requests according to the
  * routes defined in [[me.reminisce.server.StatsService]]
  */
class ServerServiceActor extends Actor with StatsServiceActor with ActorLogging {
  override def actorRefFactory = context

  val driver = new MongoDriver
  val mongoHost = ApplicationConfiguration.mongoHost
  val mongodbName = ApplicationConfiguration.mongodbName
  val connection: MongoConnection = driver.connection(List(mongoHost))

  override val db: DefaultDB = connection(mongodbName)  

  override def receive = runRoute(statsRoutes)
  
  /**
    * Cascades the shutdown to the mongo driver.
    */
  override def postStop(): Unit = {
    connection.close()
    driver.system.terminate()
    driver.close()
  }

}

