package dao

import com.mongodb.casbah.Imports._

object MongoWrapper {
  val mongoClient =  MongoClient()("site2")
}
