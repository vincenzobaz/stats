package me.reminisce.database


import scala.concurrent.Future
import spray.httpx._
import spray.json._
import DefaultJsonProtocol._
import reactivemongo.bson.{
  BSON, BSONObjectID, BSONDocument, BSONHandler, BSONDocumentReader, BSONDocumentWriter
}

object MongoDBEntities {

  import me.reminisce.dummy.DummyService._  

  abstract sealed trait Message
  case class User(id: String, name: String, age: Int) extends Message{
    override def toString(): String = s"Person: $name, ID: $id, age: $age"
  }

  case class Score(id: String, user: User, score: Double)
  case class AvgScore(name: String, score: Double){
    override def toString(): String = s"$name: $score"
    def toResultMessage(): Result = Result(name, score)
  }

  implicit object AvgScoreReader extends BSONDocumentReader[AvgScore] {
    def read(doc: BSONDocument): AvgScore = AvgScore(
      doc.getAs[String]("_id").get,
      doc.getAs[Double]("avgScore").get)
  }
  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = User(
      doc.getAs[String]("_id").get,
      doc.getAs[String]("name").get,
      doc.getAs[Int]("age").get)
  }

  implicit object  UserWriter extends BSONDocumentWriter[User]{
    def write(user: User): BSONDocument = BSONDocument(
      "id" -> user.id,
      "name" -> user.name,
      "age" -> user.age)
  }
}

