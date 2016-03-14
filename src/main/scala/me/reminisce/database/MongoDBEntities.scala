package me.reminisce.database

import reactivemongo.bson._

object MongoDBEntities {

	case class User(id: Option[BSONObjectID], name: String, age: Int)
	case class Score(id: String, user: User, score: Int)

}