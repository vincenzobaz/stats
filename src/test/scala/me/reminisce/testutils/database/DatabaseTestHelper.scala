package me.reminisce.testutils.database

import java.util.concurrent.TimeUnit

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteConcern
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONDocumentWriter, BSONObjectID}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.Source

object DatabaseTestHelper {

  private lazy val driver: MongoDriver = new MongoDriver
  private lazy val connection: MongoConnection = driver.connection(s"localhost:$port" :: Nil)

  val port = 27017

  val dbs = mutable.MutableList.empty[DefaultDB]

  def registerDb(db: DefaultDB): Unit = {
    this.synchronized {
      dbs += db
    }
  }

  def closeConnection() = {
    this.synchronized {
      dbs.foreach {
        db =>
          Await.result(db.drop(), Duration(10, TimeUnit.SECONDS))
      }
      driver.system.terminate()
    }
  }

  def getConnection: MongoConnection = {
    this.synchronized {
      connection
    }
  }

  def storeObjects[T](db: DefaultDB, collectionName: String, objects: Iterator[T])(implicit writer: BSONDocumentWriter[T]): Unit = {
    val collection = db[BSONCollection](collectionName)
    save[T](collection, objects)
  }

  def save[T](collection: BSONCollection, objs: Iterator[T])(implicit writer: BSONDocumentWriter[T]): Unit = {
    objs.foreach {
      obj =>
        collection.update(obj, obj, WriteConcern.Acknowledged, upsert = true)
    }
  }

  def simpleExtract[T](jsonPath: String)(implicit manifest: Manifest[T]): Iterator[T] = {
    implicit val formats = DefaultFormats
    val lines = Source.fromURL(getClass.getResource(jsonPath)).getLines()
    lines.map {
      l =>
        val json = parse(l)
        json.extract[T]
    }
  }

  case class FBPageLikeWithoutDate(id: Option[BSONObjectID], userId: String, pageId: String, likeTime: String)

}
