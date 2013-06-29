package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.Article
import play.api.templates.Html
import auth.Secured
import play.api.i18n.Lang
import play.api.libs.json._
import play.Logger

object ArticlesController extends Controller with Secured{

  //TODO это, разумеется, плохо и не правильно
  def groupedArticles()(implicit lang: Lang): Set[String] = {
    Article.all().filter(article => !(article.group == null || article.group.isEmpty)). map(article => article.group).toSet
  }

  def list = withUser {username => (request, user) =>
    Ok(views.html.index(groupedArticles, user.admin))
  }

  def one(id: String) = withAuth {username => implicit request =>
    Ok(Json.obj(
      "success" -> true,
      "data" -> Article.one(id)
    ))
  }

  def byGroup(group: String) = withUser {username => (request, user) =>
    Logger.info("Searching by group: user" + user)
    Logger.info("Searching by group: group" + group)
    Ok(Json.obj(
      "success" -> true,
      "data" -> Json.arr(Article.byGroup(group))
      ))
  }

  def approve(id: String) = withUser {username => (request, user) =>
    Logger.info("Appriving id: " + id + ", for user: "  + user)
    user.admin match {
      case false =>  {
        Logger.info("Do not approve")
        BadRequest(Json.obj(
          "success" -> false,
          "data" -> "Not enough permissions to approve"))
      }
      case true => {
        Logger.info("Do approve")
         Article.approve(id, approved = true)
         Ok(Json.obj(
          "success" -> true,
          "data" -> "None")
        )
      }
    }
  }

  def create = withUser {username => (request, user) =>
    Logger.info("Create: user" + user.name + ", is admin: " + user.admin)
    processRequestWithMethod(Article.create(user.admin, _, _, _))(request)
  }

  def edit(id: String) = withUser {username => (request, user) =>
    processRequestWithMethod(Article.save(id, user.admin, _, _, _))(request)
  }

  def processRequestWithMethod(method: => (String, String, String) => Unit)(implicit request: Request[AnyContent]) = {
      taskForm.bindFromRequest.fold(
        errors => BadRequest(Json.obj(
          "success" -> false,
          "data" -> errors.toString)),
        data => {
          method(data._1, data._2, data._3)
          Ok(Json.obj(
            "success" -> true,
            "data" -> "None")
          )
        }
      )
  }

  def delete(id: String) = withAuth {username => implicit request =>
    Article.delete(id)
    Ok(Json.obj(
      "success" -> true,
      "data" -> "None")
    )
  }

  val taskForm = Form( tuple(
    "caption" -> nonEmptyText,
    "group" -> text,
    "body" -> nonEmptyText
  )
  )
}
