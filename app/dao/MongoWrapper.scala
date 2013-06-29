package dao

import com.mongodb.casbah.Imports._
import play.api.i18n.Messages

object MongoWrapper {
  private val mongoClientName =  "site2"

  private def executeQuery[T](collectionName: String, action: (MongoCollection) => T): T = {
    val mongo = MongoConnection("localhost", 27017)
    val mongoClient = mongo(mongoClientName)
    val collection = mongoClient(collectionName)
    try action(collection)
    finally mongo.underlying.close()
  }

  def insert(collectionName: String, objectToInsert: MongoDBObject){
    executeQuery(collectionName, (collection: MongoCollection) => collection.insert(objectToInsert))
  }

  def find[T](collectionName: String, converter: (DBObject) => T, query: Option[MongoDBObject] = None): List[T] = {
    executeQuery[List[T]](collectionName,
      (collection: MongoCollection) => (for {x <- query match {
        case None => collection.find()
        case Some(dQuery) => collection.find(dQuery)
      } } yield converter(x)).toList)
  }

  def find2[T](collectionName: String, converter: (DBObject) => T, query: MongoDBObject): List[T] = {
    executeQuery[List[T]](collectionName,
      (collection: MongoCollection) => (for {x <- collection.find(query)} yield converter(x)).toList)
  }

  def findOne[T](collectionName: String, query: MongoDBObject, converter: (DBObject) => T): T = {
    executeQuery[T](collectionName, (collection: MongoCollection) => collection.findOne(query) match {
      case Some(result) => converter(result)
      case None => throw new Exception(Messages("ObjectNotFoundException"))
    })
  }

  def update(collectionName: String, query: MongoDBObject, objectToUpdate: MongoDBObject){
    executeQuery(collectionName, (collection: MongoCollection) => collection.update(query, objectToUpdate))
  }

  def remove(collectionName: String, query: MongoDBObject) {
    executeQuery(collectionName, (collection: MongoCollection) => collection.remove(query))
  }

}
