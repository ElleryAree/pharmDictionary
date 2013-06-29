package models

import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.{JsObject, JsValue, Json, Format}
import play.Logger
import java.util.{GregorianCalendar, Date}
import play.api.libs.json.JsObject

case class Article(id: ObjectId, caption: String, group: String, body: String) {

}

/*object ArticleFormat extends Format[Article] {

}*/

object Article {
  val articleCollection = "articles"

  def byIdQuery(id: String) = MongoDBObject("_id" -> new ObjectId(id))
  def byGroupQuery(group: String) = MongoDBObject("group" -> group)

  val format = new java.text.SimpleDateFormat("dd.MM.yyyy hh.mm.ss")

  def mongoObjectFromArticle(caption: String, group: String, body: String, approved: Boolean) =
    MongoDBObject("caption" -> caption, "group" -> group, "body" -> body, "last_update" -> new Date(), "approved" -> approved)

  def articleFromDBObject(source: DBObject)(implicit lang: Lang): Article = {
    Article(new ObjectId(source.get("_id").toString),
      source.getOrElse("caption", "").toString,
      source.getOrElse("group", "").toString,
      source.getOrElse("body", "").toString)
  }

  def jsonFromDBObject(source: DBObject) = {
    Logger.info("Converting an object: " + source)
    Json.obj(
      "id" -> source.get("_id").toString,
      "caption" -> source.getOrElse("caption", "").toString,
      "body" -> source.getOrElse("body", "").toString,
      "group" -> source.getOrElse("group", "").toString,
      "approved" -> source.getOrElse("approved", "false").toString,
      "last_update" -> format.format(source.getOrElse("last_update", new GregorianCalendar(1970, 1, 0, 0, 0).getTime))
    )
  }

  def all()(implicit lang: Lang): List[Article] = {
    MongoWrapper.find[Article](articleCollection, articleFromDBObject)
  }

  def byGroup(group: String): List[JsObject]  = {
    MongoWrapper.find2[JsObject](articleCollection, jsonFromDBObject, byGroupQuery(group))
  }

  def create(approved: Boolean, caption: String, group: String, body: String) {
    MongoWrapper.insert(articleCollection, mongoObjectFromArticle(caption, group, body, approved))
  }

  def save(id: String, approved: Boolean, caption: String, group: String, body: String) {
    MongoWrapper.update(articleCollection, byIdQuery(id), mongoObjectFromArticle(caption, group, body, approved))
  }

  def approve(id: String, approved: Boolean) {
    MongoWrapper.update(articleCollection, byIdQuery(id), MongoDBObject("$set" -> MongoDBObject("approved" -> approved)))
  }

  def delete(id: String) {
    MongoWrapper.remove(articleCollection, byIdQuery(id))
  }

  def one(id: String)(implicit lang: Lang): JsObject = {
    MongoWrapper.findOne[JsObject](articleCollection, byIdQuery(id), jsonFromDBObject)
  }
}

