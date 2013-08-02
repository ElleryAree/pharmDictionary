package models

import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._
import com.mongodb.casbah.commons.TypeImports.ObjectId
import play.api.i18n.Lang
import play.api.libs.json.Json
import java.util.{GregorianCalendar, Date}
import play.api.libs.json.JsObject
import lucene.LuceneWrapper
import play.api.Logger

case class Article(id: ObjectId, caption: String, short_description: String, group: String, body: String) {

}

/*object ArticleFormat extends Format[Article] {

}*/

object Article {
  val articleCollection = "articles"

  def byIdQuery(id: String) = MongoDBObject("_id" -> new ObjectId(id))
  def byGroupQuery(group: String) = MongoDBObject("group" -> group)

  val format = new java.text.SimpleDateFormat("dd.MM.yyyy hh.mm.ss")

  def mongoObjectFromArticle(caption: String, short_description: String, group: String, body: String, approved: Boolean) ={
    MongoDBObject("caption" -> caption, "short_description" -> short_description, "group" -> group, "body" -> body, "last_update" -> new Date(), "approved" -> approved)
  }

  def articleFromDBObject(source: DBObject)(implicit lang: Lang): Article = {
    Article(new ObjectId(source.get("_id").toString),
      source.getOrElse("caption", "").toString,
      source.getOrElse("short_description", "").toString,
      source.getOrElse("group", "").toString,
      source.getOrElse("body", "").toString)
  }

  def jsonFromDBObject(source: DBObject) = {
//    Logger.info("Converting an object: " + source)
    jsonFromStrings(
      source.get("_id").toString,
      source.getOrElse("caption", "").toString,
      source.getOrElse("short_description", "").toString,
      source.getOrElse("body", "").toString,
      source.getOrElse("group", "").toString,
      source.getOrElse("approved", "false").toString,
      format.format(source.getOrElse("last_update", new GregorianCalendar(1970, 1, 0, 0, 0).getTime))
    )
  }

  def jsonFromStrings(id: String, caption: String, description: String, body: String, group: String, approved: String, last_update: String) = {
    Json.obj(
      "id" -> id,
      "caption" -> caption,
      "short_description" -> description,
      "body" -> body,
      "group" -> group,
      "approved" -> approved,
      "last_update" -> last_update)
  }

  def all()(implicit lang: Lang): List[Article] = {
    MongoWrapper.find[Article](articleCollection, articleFromDBObject)
  }

  def byGroup(group: String): List[JsObject]  = {
    MongoWrapper.find2[JsObject](articleCollection, jsonFromDBObject, byGroupQuery(group))
  }

  def create(approved: Boolean, caption: String, shortDescription: String, group: String, body: String) {
    def res = MongoWrapper.insert(articleCollection, mongoObjectFromArticle(caption, shortDescription, group, body, approved))
    Logger.info("Res: " + res)
  }

  def save(id: String, approved: Boolean, caption: String, shortDescription: String, group: String, body: String) {
    LuceneWrapper.addDocument(id, caption, shortDescription, body)
    MongoWrapper.update(articleCollection, byIdQuery(id), mongoObjectFromArticle(caption, shortDescription, group, body, approved))
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

