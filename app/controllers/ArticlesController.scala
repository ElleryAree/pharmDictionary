package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.Article
import play.api.templates.Html
import auth.Secured
import play.api.i18n.Lang

object ArticlesController extends Controller with Secured{

  def groupedArticles()(implicit lang: Lang): Map[String, List[Article]] = {
    Article.all().groupBy(article => article.group)
  }

  def list = withAuth {username => implicit request =>
    Ok(views.html.index(groupedArticles, taskForm))
  }

  def one(id: String) = withAuth {username => implicit request =>
    try{
      val article = Article.one(id)
      val dataForForm = Map("caption" -> article.caption, "group" -> article.group, "body" -> article.body)
      Ok(views.html.article(article, taskForm.bind(dataForForm)))
    }
    catch { case e: Throwable => { BadRequest(views.html.index(groupedArticles, taskForm)) } }
  }

  def create = withAuth {username => implicit request =>
    processRequestWithMethod(Article.create,
                                          views.html.index(groupedArticles, _),
                                          routes.ArticlesController.list())
  }

  def edit(id: String) = withAuth {username => implicit request =>
    try {
        processRequestWithMethod(Article.save(id, _, _, _),
          views.html.article(Article.one(id), _),
          routes.ArticlesController.one(id))
    }
    catch { case e: Throwable => { BadRequest(views.html.index(groupedArticles, taskForm)) } }
  }

  def processRequestWithMethod(method: => (String, String, String) => Unit,
                               errorMethod: => (Form[(String, String, String)]) => Html,
                               successMethod: Call)(implicit request: Request[AnyContent]) = {
      taskForm.bindFromRequest.fold(
        errors => BadRequest(errorMethod(errors)),
        data => {
          method(data._1, data._2, data._3)
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
    "group" -> nonEmptyText,
    "body" -> nonEmptyText
  )
  )
}
