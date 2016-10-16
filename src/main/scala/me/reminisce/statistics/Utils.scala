package me.reminisce.statistics

import reactivemongo.bson._
import com.github.nscala_time.time.Imports._
import me.reminisce.server.GameEntities.QuestionKind.QuestionKind


object Utils{
  
  implicit object DatetimeReader extends BSONReader[BSONDateTime, DateTime]{
    def read(bson: BSONDateTime): DateTime = {
      val time = new DateTime(bson.value)
      time
    }
  }

  implicit object DatetimeWriter extends BSONWriter[DateTime, BSONDateTime]{
    def write(t: DateTime): BSONDateTime = {
      BSONDateTime(t.getMillis)
    }
  }
}