package models

import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import play.api.i18n.{Lang, Messages}

case class Article(id: ObjectId, caption: String, group: String, body: String) {
}

object Article {
  val articleCollection = "articles"

  def byId(id: String) = MongoDBObject("_id" -> new ObjectId(id))
  def mongoObjectFromArticle(caption: String, group: String, body: String) =
    MongoDBObject("caption" -> caption, "group" -> group, "body" -> body)

  def articleFromDBObject(source: DBObject)(implicit lang: Lang): Article = {
    Article(new ObjectId(source.get("_id").toString),
      source.getOrElse("caption", "").toString,
      source.getOrElse("group", Messages("articleNoGroup")).toString,
      source.getOrElse("body", "").toString)
  }

  def all()(implicit lang: Lang): List[Article] = {
    MongoWrapper.find[Article](articleCollection, articleFromDBObject)
  }

  def create(caption: String, group: String, body: String) {
    MongoWrapper.insert(articleCollection, mongoObjectFromArticle(caption, group, body))
  }

  def save(id: String, caption: String, group: String, body: String) {
    MongoWrapper.update(articleCollection, byId(id), mongoObjectFromArticle(caption, group, body))
  }

  def delete(id: String) {
    MongoWrapper.remove(articleCollection, byId(id))
  }

  def one(id: String)(implicit lang: Lang): Article = {
    MongoWrapper.findOne[Article](articleCollection, byId(id), articleFromDBObject)
  }
}
