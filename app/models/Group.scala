package models

import play.api.i18n.Lang
import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._
import org.bson.types.ObjectId
import play.api.libs.json.{JsObject, Json}

case class Group(id: ObjectId, name: String){
}

object Group{
  val groupsCollection = "groups"

  def groupFromDBObject(source: DBObject)(implicit lang: Lang): Group = {
    Group(new ObjectId(source.get("_id").toString),
      source.getOrElse("name", "").toString)
  }
  def mongoObjectFromGroup(caption: String) =
    MongoDBObject("name" -> caption)

  def jsonFromDBObject(source: DBObject) = {
    Json.obj(
      "id" -> source.get("_id").toString,
      "name" -> source.getOrElse("name", "").toString
    )
  }

  def all()(implicit lang: Lang): List[JsObject] = {
    MongoWrapper.find[JsObject](groupsCollection, jsonFromDBObject)
  }

  def create(name: String) {
    MongoWrapper.insert(groupsCollection, mongoObjectFromGroup(name))
  }

  def save(id: String, name: String) {
    MongoWrapper.update(groupsCollection, MongoUtils.byIdQuery(id), mongoObjectFromGroup(name))
  }
}
