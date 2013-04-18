package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.Article
import play.api.templates.Html
import play.api.i18n.{Messages, Lang}
import auth.Secured

object ArticlesController extends Controller with Secured{
  def list = withAuth {username => implicit request =>
    Ok(views.html.index(Article.all(), taskForm))
  }
  def one(id: String) = withAuth {username => implicit request =>
    Article.one(id) match {
      case Some(article) => {
        val dataForForm = Map("caption" -> article.caption, "body" -> article.body)
        Ok(views.html.article(article, taskForm.bind(dataForForm)))
      }
      case None => BadRequest(views.html.index(Article.all(), taskForm))
    }
  }
  def create = withAuth {username => implicit request =>
    processRequestWithMethod(Article.create,
                                          views.html.index(Article.all(), _),
                                          routes.ArticlesController.list())
  }

  def edit(id: String) = withAuth {username => implicit request =>
    Article.one(id) match {
      case Some(article) => {
        processRequestWithMethod(Article.save(id, _, _),
          views.html.article(article, _),
          routes.ArticlesController.one(id))
      }
      case _ => BadRequest(views.html.index(Article.all(), taskForm))
    }
  }

  def processRequestWithMethod(method: => (String, String) => Unit,
                               errorMethod: => (Form[(String, String)]) => Html,
                               successMethod: Call)(implicit request: Request[AnyContent]) = {
      taskForm.bindFromRequest.fold(
        errors => BadRequest(errorMethod(errors)),
        data => {
          method(data._1, data._2)
          Redirect(successMethod)
        }
  )
  }

  def delete(id: String) = withAuth {username => implicit request =>
    Article.delete(id)
    Redirect(routes.ArticlesController.list())
  }

  val taskForm = Form( tuple(
    "caption" -> nonEmptyText,
    "body" -> nonEmptyText
    )
  )
}
