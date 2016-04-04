package me.reminisce.server

import me.reminisce.server.GameEntities._
import org.json4s.JsonAST.JField
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s._

class SubjectSerializer extends CustomSerializer[Subject](implicit formats => ( {
  case JObject(List(JField("name", JString(name)), JField("pageId", JString(pageId)), JField("photoUrl", JString(photoUrl)),
  JField("type", JString(tpe)))) => PageSubject(name, pageId, Some(photoUrl), SubjectType.PageSubject)

  case JObject(List(JField("comment", JString(comment)), JField("post", post), JField("type", JString(tpe)))) =>
    CommentSubject(comment, Some(post.extractOpt[Subject].asInstanceOf[PostSubject]), SubjectType.CommentSubject)

  case JObject(List(JField("text", JString(text)), JField("type", JString(tpe)), JField("from", from))) =>
    TextPostSubject(text, SubjectType.TextPost, from.extractOpt[FBFrom])

  case JObject(List(JField("text", JString(text)), JField("imageUrl", JString(imageUrl)), JField("facebookImageUrl", JString(facebookImageUrl)),
  JField("type", JString(tpe)), JField("from", from))) =>
    ImagePostSubject(text, Some(imageUrl), Some(facebookImageUrl), SubjectType.ImagePost, from.extractOpt[FBFrom])

  case JObject(List(JField("text", JString(text)), JField("thumbnailUrl", JString(thumbnailUrl)), JField("url", JString(url)),
  JField("type", JString(tpe)), JField("from", from))) =>
    if (tpe.equals("VideoPost"))
      VideoPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.VideoPost, from.extractOpt[FBFrom])
    else
      LinkPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.LinkPost, from.extractOpt[FBFrom])

  case JObject(List(JField("text", JString(text)), JField("thumbnailUrl", JString(thumbnailUrl)), JField("url", JString(url)),
  JField("type", JString(tpe)), JField("from", from))) =>
    LinkPostSubject(text, Some(thumbnailUrl), Some(url), SubjectType.LinkPost, from.extractOpt[FBFrom])

}, {
  case PageSubject(name, pageId, photoUrl, tpe) => JObject(List(JField("name", JString(name)), JField("pageId", JString(pageId)),
    JField("photoUrl", JString(photoUrl.toString)), JField("type", JString(tpe.toString))))

  case CommentSubject(comment, post, tpe) => JObject(List(JField("comment", JString(comment)),
    JField("post", JString(post.toString)), JField("type", JString(tpe.toString))))

  case TextPostSubject(text, tpe, from) => JObject(List(JField("text", JString(text)), JField("type", JString(tpe.toString)),
    JField("from", JString(from.toString))))

  case ImagePostSubject(text, imageUrl, facebookImageUrl, tpe, from) => JObject(List(JField("text", JString(text)),
    JField("imageUrl", JString(imageUrl.toString)), JField("facebookImageUrl", JString(facebookImageUrl.toString)),
    JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))

  case LinkPostSubject(text, thumbnailUrl, url, tpe, from) => JObject(List(JField("text", JString(text)),
    JField("thumbnailUrl", JString(thumbnailUrl.toString)), JField("url", JString(url.toString)),
    JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))

  case VideoPostSubject(text, thumbnailUrl, url, tpe, from) => JObject(List(JField("text", JString(text)),
    JField("thumbnailUrl", JString(thumbnailUrl.toString)), JField("url", JString(url.toString)),
    JField("type", JString(tpe.toString)), JField("from", JString(from.toString))))
}))
