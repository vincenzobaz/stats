package me.reminisce

import org.scalatest.FunSuite
import me.reminisce.server.GameEntities._
import reactivemongo.bson._


class BSONSerializersTest extends FunSuite {

  // ***** MOVE *****
  val move = Move(2,1)
  val docMove = BSONDocument(
      "row" -> 2,
      "column" -> 1
      )

  test("MoveWrite") {
    val bson = BSON.writeDocument(move)
    assert(bson == docMove)
  }  
  test("MoveRead") {
    val result = docMove.as[Move]
    assert(result == move)
  }
  test("MoveWriteRead"){
    val bson = BSON.writeDocument(move)
    val result = bson.as[Move]
    assert(move == result)
  }

  // ***** SCORE *****
  val docScore = BSONDocument(
      "player" -> 1,
      "score" -> 3456
      )
  val score = Score(1, 3456)

  test("ScoreWrite") {
    val bson = BSON.writeDocument(score)
    assert(bson == docScore)
  }
  test("ScoreRead") {    
    val result = docScore.as[Score]
    assert(result == score)
  }
  test("ScoreWriteRead"){
    val bson = BSON.writeDocument(score)
    val result = bson.as[Score]
    assert(score == result)
  }

  // ***** LOCATION *****
  val docLocation = BSONDocument(
    "latitude" -> 1.5,
    "longitude" -> 0.003456
    )
  val location = Location(1.5, 0.003456)

  test("LocationWrite") {
    val bson = BSON.writeDocument(location)
    assert(bson == docLocation)
  }
  test("LocationRead") {    
    val result = docLocation.as[Location]
    assert(result == location)
  }
  test("LocationWriteRead"){
    val bson = BSON.writeDocument(location)
    val result = bson.as[Location]
    assert(location == result)
  }

  // ***** FBFROM *****
  val docFBFrom = BSONDocument(
    "userId" -> "id12345",
    "userName" -> "audreyloeffel"
  )
  val fbFrom = FBFrom("id12345", "audreyloeffel")

  test("FBFromWrite") {
    val bson = BSON.writeDocument(fbFrom)
    assert(bson == docFBFrom)
  }
  test("FBFromRead") {    
    val result = docFBFrom.as[FBFrom]
    assert(result == fbFrom)
  }
  test("FBFromWriteRead"){
    val bson = BSON.writeDocument(fbFrom)
    val result = bson.as[FBFrom]
    assert(fbFrom == result)
  }

 // ***** PAGESUBJECT *****
  val docPageSubject = BSONDocument(
    "name" -> Some("Blood Bowl"),
    "pageId" -> "13590131663",
    "photoUrl" -> Some("https://reminisce.me"),
    "type" -> SubjectType.PageSubject.toString
    )
   val pageSubject = PageSubject("Blood Bowl", 
    "13590131663", 
    Some("https://reminisce.me"))
    println(docPageSubject)
  test("PageSubjectWrite") {
    val bson = BSON.writeDocument[PageSubject](pageSubject)
    println(bson)
    assert(bson.getAs[String]("name") == docPageSubject.getAs[String]("name"))
    assert(bson.getAs[String]("pageId") == docPageSubject.getAs[String]("pageId"))
    assert(bson.getAs[String]("photoUrl") == docPageSubject.getAs[String]("photoUrl"))
    assert(bson.getAs[String]("type") == docPageSubject.getAs[String]("type"))
  //  assert(bson == docPageSubject)
  }

  test("PageSubjectRead") {   
    val result = docPageSubject.as[PageSubject]
    assert(result == pageSubject)
  }
  
test("PageSubjectWriteRead"){
  val bson = BSON.writeDocument(pageSubject)
  val result = bson.as[PageSubject]
  assert(pageSubject == result)
}  

// ***** SUBJECTWITHID ****
val docSubjectWithID = BSONDocument(
  "uId" -> 12830,
  "subject" -> SubjectWriter.write(pageSubject)
  )
val subjectWithID = SubjectWithId(12830, Some(pageSubject))

test("SubjectWithIDWrite") {
  val bson = BSON.writeDocument(subjectWithID)
  assert(bson == docSubjectWithID)
}

test("SubjectWithIDRead") {    
  val result = docSubjectWithID.as[SubjectWithId]
  assert(result == subjectWithID)
}

test("SubjectWithIDWriteRead"){
  val bson = BSON.writeDocument(subjectWithID)
  val result = bson.as[SubjectWithId]
  assert(subjectWithID == result)
}  

// ***** GEOLOCATIONQUESTION *****
val docGeoLocationQuestion = BSONDocument(
  "subject" -> SubjectWriter.write(pageSubject),
  "range" -> 0.2,
  "defaultLocation" -> location,
  "answer" -> location,
  "type" -> SubjectType.PageSubject,
  "kind" -> QuestionKind.Geolocation
  )
val geoLocationQuestion = GeolocationQuestion(
  Some(pageSubject), 
  0.2, 
  location, 
  location, 
  SubjectType.PageSubject.toString
  )

test("GeolocationQuestionWrite") {
  val bson = BSON.writeDocument(geoLocationQuestion)
//  val bson = GameQuestionWriter.write(geoLocationQuestion)
  assert(bson.getAs[Subject]("subject") == docGeoLocationQuestion.getAs[Subject]("subject"))
  assert(bson.getAs[Double]("range") == docGeoLocationQuestion.getAs[Double]("range"))
  assert(bson.getAs[Location]("defaultLocation") == docGeoLocationQuestion.getAs[Location]("defaultLocation"))
  assert(bson.getAs[Location]("answer") == docGeoLocationQuestion.getAs[Location]("answer"))
  assert(bson.getAs[String]("type") == docGeoLocationQuestion.getAs[String]("type"))
  assert(bson.getAs[String]("kind") == docGeoLocationQuestion.getAs[String]("kind"))
  //assert(bson == docGeoLocationQuestion) 
 // TODO why we can not compare the whole documents ?
}

test("GeolocationQuestionRead") {    

  val result = docGeoLocationQuestion.as[GeolocationQuestion]
  assert(result == geoLocationQuestion)
}

test("GeolocationQuestionWriteRead"){

  val bson = BSON.writeDocument(geoLocationQuestion)
  val result = bson.as[GeolocationQuestion]
  assert(geoLocationQuestion == result)
}  

// ***** TILE *****
  
val docTile = BSONDocument(
  "type" -> SubjectType.PageSubject,
  "_id" -> "id12345",
  "question1" -> BSON.write(geoLocationQuestion), 
  "question2" -> docGeoLocationQuestion,
  "question3" -> docGeoLocationQuestion,
  "score" -> 2341,
  "answered" -> true, 
  "disabled" -> false
  )
val tile = Tile(
  SubjectType.PageSubject.toString(), 
  "id12345", 
  Some(geoLocationQuestion),
  Some(geoLocationQuestion),
  Some(geoLocationQuestion),
  2341, true, false
  )
test("TileWrite") {
  val bson = BSON.writeDocument(tile)
  assert(docTile.getAs[Subject]("subject") == bson.getAs[Subject]("subject"))
  assert(docTile.getAs[String]("_id") == bson.getAs[String]("_id"))
  assert(docTile.getAs[GameQuestion]("question1") == bson.getAs[GameQuestion]("question1"))
  assert(docTile.getAs[GameQuestion]("question2") == bson.getAs[GameQuestion]("question2"))
  assert(docTile.getAs[GameQuestion]("question3") == bson.getAs[GameQuestion]("question3"))
  assert(docTile.getAs[Int]("score") == bson.getAs[Int]("score"))
  assert(docTile.getAs[Boolean]("answered") == bson.getAs[Boolean]("answered"))
  assert(docTile.getAs[Boolean]("disabled") == bson.getAs[Boolean]("disabled"))
  //assert(bson == docTile)
}

test("TileRead") {    
  val result = docTile.as[Tile]
  assert(result == tile)
}

test("TileWriteRead"){
  val bson = BSON.writeDocument(docTile)
  val result = bson.as[Tile]
  assert(tile == result)
}  

}