package models

import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId

case class Article(id: ObjectId, caption: String, body: String) {
}

object Article {
  val articleCollection = MongoWrapper.mongoClient("articles")

  def byId(id: String) = MongoDBObject("_id" -> new ObjectId(id))

  def articleFromDBObject(source: DBObject): Article = {
    Article(new ObjectId(source.get("_id").toString), source.get("caption").toString, source.get("body").toString)
  }

  def all(): List[Article] = {
    (for {x <- articleCollection.find()} yield articleFromDBObject(x)).toList
  }

  def create(caption: String, body: String) {
    articleCollection.insert(MongoDBObject("caption" -> caption, "body" -> body))
  }

  def save(id: String, caption: String, body: String) {
    articleCollection.update(byId(id), MongoDBObject("caption" -> caption, "body" -> body))
  }

  def delete(id: String) {
    articleCollection.remove(byId(id))
  }

  def one(id: String): Option[Article] = {
    articleCollection.findOne(byId(id)) match {
      case None => None
      case Some(rawArticle) => Some(articleFromDBObject(rawArticle))
    }

  }
}
