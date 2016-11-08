package me.reminisce.testutils.database

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, WordSpecLike}
import reactivemongo.api.{DefaultDB, FailoverStrategy}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Random

abstract class DatabaseTester(actorSystemName: String) extends TestKit(ActorSystem(actorSystemName, ConfigFactory.load()))
  with ImplicitSender with ScalaFutures
  with WordSpecLike with BeforeAndAfterAll with BeforeAndAfterEach {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  val failoverStrategy = FailoverStrategy(initialDelay = FiniteDuration(200, TimeUnit.MILLISECONDS), retries = 10)

  val dbs = mutable.MutableList.empty[DefaultDB]

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
    closeDBs()
  }

  override def afterEach(): Unit = {
    Thread.sleep(100)
  }

  protected def testWithDb(test: DefaultDB => Unit): Unit = {
    val dbId = Random.nextInt // if the actorSystemName is shared for unknown reasons.
    val connection = DatabaseTestHelper.getConnection
    val dbName = s"DB${dbId}_for_$actorSystemName"

    whenReady(connection.database(dbName, failoverStrategy = failoverStrategy)) {
      db =>
        registerDb(db)
        test(db)
    }
  }

  def registerDb(db: DefaultDB): Unit = {
    dbs += db
  }

  def closeDBs(): Unit = {
    dbs.foreach {
      db =>
        Await.result(db.drop(), Duration(10, TimeUnit.SECONDS))
    }
  }
}
