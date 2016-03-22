package me.reminisce.database

import reactivemongo.bson.{
  BSON, BSONObjectID, BSONDocument, BSONHandler, BSONDocumentReader, BSONDocumentWriter
}

object MongoDBEntities {
  import me.reminisce.dummy.DummyService._

	case class User(id: Option[BSONObjectID], name: String, age: Int)
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

}