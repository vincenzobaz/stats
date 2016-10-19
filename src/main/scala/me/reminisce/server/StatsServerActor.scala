package me.reminisce.server

import akka.actor.{Actor, ActorLogging}
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * The actor handling the http request in this application. It handles the requests according to the
  * routes defined in [[me.reminisce.server.StatsService]]
  */
class StatsServerActor(mongoHost: String, mongoDbName: String) extends Actor with StatsServiceActor with ActorLogging {
  override def actorRefFactory = context

  val driver = new MongoDriver
  override val dbName = mongoDbName
  override val dbConnection: MongoConnection = driver.connection(List(mongoHost))


  override def receive = runRoute(statsRoutes)

  /**
    * Cascades the shutdown to the mongo driver.
    */
  override def postStop(): Unit = {
    dbConnection.close()
    driver.system.terminate()
    driver.close()
  }

}

