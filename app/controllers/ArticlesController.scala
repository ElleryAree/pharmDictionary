package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.Article
import play.api.templates.Html
import play.api.i18n.{Messages, Lang}

object ArticlesController extends Controller{
  def list = Action{
    Ok(views.html.index(Article.all(), taskForm))
  }
  def one(id: String) = Action {
    Article.one(id) match {
      case Some(article) => {
        val dataForForm = Map("caption" -> article.caption, "body" -> article.body)
        Ok(views.html.article(article, taskForm.bind(dataForForm)))
      }
      case None => BadRequest(views.html.index(Article.all(), taskForm))
    }
  }
  def create: Action[AnyContent] = { processRequestWithMethod(Article.create,
                                          views.html.index(Article.all(), _),
                                          routes.ArticlesController.list())
  }

  def edit(id: String): Action[AnyContent] =  {
    Article.one(id) match {
      case Some(article) => {
        processRequestWithMethod(Article.save(id, _, _),
          views.html.article(article, _),
          routes.ArticlesController.one(id))
      }
      case _ => Action(BadRequest(views.html.index(Article.all(), taskForm)))
    }
  }

  def processRequestWithMethod(method: => (String, String) => Unit,
                               errorMethod: => (Form[(String, String)]) => Html,
                               successMethod: Call): Action[AnyContent] = { Action(
    implicit request =>
      taskForm.bindFromRequest.fold(
        errors => BadRequest(errorMethod(errors)),
        data => {
          method(data._1, data._2)
          Redirect(successMethod)
        }
      )
  )
  }

  def delete(id: String) = Action {
    Article.delete(id)
    Redirect(routes.ArticlesController.list())
  }

  val taskForm = Form( tuple(
    "caption" -> nonEmptyText,
    "body" -> nonEmptyText
    )
  )
}
