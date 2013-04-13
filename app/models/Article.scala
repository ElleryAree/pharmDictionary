package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import dao.MongoWrapper
import com.mongodb.casbah.commons.Imports._

case class Article(id: Int, caption: String, body: String) {
}

object Article {
  val article = {
    get[Int]("id") ~
      get[String]("caption") ~
        get[String]("body") map {
      case id~caption~body => Article(id, caption, body)
    }
  }

  def all(): List[Article] = DB.withConnection { implicit c =>
    SQL("select * from article").as(article *)
  }
  def create(caption: String, body: String) { DB.withConnection { implicit c =>
    SQL("insert into article (caption, body) values ({caption}, {body})").on(
      'caption -> caption, 'body -> body
    ).executeUpdate()
  }
  }
  def delete(id: Int) {
    DB.withConnection { implicit c =>
      SQL("delete from article where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  def one(id: Int): Article = {
    DB.withConnection { implicit c =>
      SQL("select * from article where id = {id}").on(
        'id -> id
      ).as(article *).head
    }
  }
}

object MongoArticle {
  val articleCollection = MongoWrapper.mongoClient("articles")

  def all(): List[Article] = {
//    for {x <- articleCollection.find()} yield Article(x.get("id"), x.get("caption"), x.get("body"))
//    for {x <- articleCollection.find()} yield x.getAs[Article]()
//    for {x <- articleCollection.find()} printf(x.getClass() + ": " + x.toString())
//    val aOne = articleCollection.findOne()
//    println(aOne.get(0))
//    Nil
    articleCollection.find(MongoDBObject.empty).toList
  }
  def create(caption: String, body: String) {}
  def delete(id: Int) {}
}
