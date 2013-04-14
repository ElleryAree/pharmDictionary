package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.{Article}

object ArticlesController extends Controller{
  def list = Action{
    Ok(views.html.index(Article.all(), taskForm))
  }
  def one(id: String) = Action {
    Article.one(id) match {
      case Some(article) => Ok(views.html.article(article))
      case None => BadRequest(views.html.index(Article.all(), taskForm))
    }
  }
  def create = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(Article.all(), errors)),
      data  => {
        Article.create(data._1, data._2)
        Redirect(routes.ArticlesController.list())
      }
    )
  }
  def delete(id: String) = Action {
    Article.delete(id)
    Redirect(routes.ArticlesController.list)
  }

  val taskForm = Form( tuple(
    "caption" -> nonEmptyText,
    "body" -> nonEmptyText
    )
  )
}
