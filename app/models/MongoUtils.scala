package models

import com.mongodb.casbah.commons.Imports._
import org.bson.types.ObjectId

object MongoUtils {
  def byIdQuery(id: String) = MongoDBObject("_id" -> new ObjectId(id))
}
