package controllers

import play.api.mvc._
import play.api.i18n.Lang
import play.api.Play.current
import scala.Some

object Application extends Controller {
  
  def index = Action {
    Redirect(routes.ArticlesController.list())
  }

  def changeLanguage(code: String) = Action {
    implicit request =>
    Lang.get(code) match {
      case Some(lang) => Redirect(request.headers.get(REFERER).getOrElse("/")).withLang(lang)
      case None => InternalServerError(views.html.error("Language change", "No language with code:" + code))
    }
  }
}