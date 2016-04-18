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
    "name" -> "Blood Bowl",
    "pageId" -> "13590131663",
    "photoUrl" -> Some("https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0"),
    "type" -> SubjectType.PageSubject
    )
  val pageSubject = PageSubject("Blood Bowl", 
    "13590131663", 
    Some("https://scontent.xx.fbcdn.net/hphotos-xaf1/v/t1.0-9/1929960_13590436663_114_n.jpg?oh=25eae23b71e482c85c7fb68d768ab4fa&oe=5632DFF0"),
    SubjectType.PageSubject)

//TODO: Why it doesn't work with BSON.writeDocument ?
  test("PageSubjectWrite") {
   // val bson = SubjectWriter.write(pageSubject)
    val bson = BSON.writeDocument[PageSubject](pageSubject)
    assert(bson == docPageSubject)
  }
  test("PageSubjectRead") {    
    val result = SubjectReader.read(docPageSubject)
    //val result = docPageSubject.as[PageSubject]
    assert(result == pageSubject)
  }
  test("PageSubjectWriteRead"){
    val bson = SubjectWriter.write(pageSubject)
    val result = SubjectReader.read(bson)
    //val bson = BSON.writeDocument(pageSubject)
    //val result = bson.as[Subject]
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
    SubjectType.PageSubject.toString,
    QuestionKind.Geolocation
    )

  test("GeolocationQuestionWrite") {
    //val bson = BSON.writeDocument(geoLocationQuestion)
    val bson = GameQuestionWriter.write(geoLocationQuestion)
    assert(bson == docGeoLocationQuestion)
  }

  test("GeolocationQuestionRead") {    
    val result = GameQuestionReader.read(docGeoLocationQuestion)
    //val result = docGeoLocationQuestion.as[GeolocationQuestion]
    assert(result == geoLocationQuestion)
  }

  test("GeolocationQuestionWriteRead"){
    val bson = GameQuestionWriter.write(geoLocationQuestion)
    val result = GameQuestionReader.read(bson)
   // val bson = BSON.writeDocument(geoLocationQuestion)
   // val result = bson.as[GeolocationQuestion]
    assert(geoLocationQuestion == result)
  }  

  // ***** TILE *****
  val docTile = BSONDocument(
    "type" -> SubjectType.PageSubject,
    "_id" -> "id12345",
    "question1" -> geoLocationQuestion, //GameQuestionReader.read(docGeoLocationQuestion),
    "question2" -> GameQuestionReader.read(docGeoLocationQuestion),
    "question3" -> GameQuestionReader.read(docGeoLocationQuestion),
    "score" -> 2341,
    "answered" -> true, 
    "disabled" -> false
    )
  val tile = Tile(
    SubjectType.PageSubject.toString, 
    "id12345", 
    Some(geoLocationQuestion),
    Some(geoLocationQuestion),
    Some(geoLocationQuestion),
    2341, true, false
    )
  test("TileWrite") {
    val bson = BSON.writeDocument(tile)
    assert(bson == docTile)
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