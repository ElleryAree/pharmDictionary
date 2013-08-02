package controllers

import play.api.mvc._

import auth.Secured
import play.api.libs.json._
import lucene.LuceneWrapper
import scala.collection.JavaConversions._
import models.Article

object SearchController extends Controller with Secured{

  def search(query: String) = withAuth {username => implicit request =>
    Ok(Json.obj(
      "success" -> true,
      "data" -> Json.arr(doSearch(query).map(article => Article.jsonFromStrings(
        article.id.toString,
        article.caption,
        article.short_description,
        article.body,
        article.group,
        "true",
        ""))))
    )
  }

  def doReindex() = withAuth {username => implicit request => {
      LuceneWrapper.addAll(Article.all())
      Ok(Json.obj(
        "success" -> true,
        "data" -> "Reindex complete")
      )
    }
  }

  def doSearch(query: String)= LuceneWrapper.search(query).toList

}
