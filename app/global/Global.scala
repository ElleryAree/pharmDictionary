package global

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.i18n.Messages

class Global extends GlobalSettings {

  override def onError(request: RequestHeader, ex: Throwable) = {
    InternalServerError(views.html.error(Messages("DBErrorCaption"), ex.getLocalizedMessage))
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    BadRequest(views.html.error(Messages("DBErrorCaption"), error))
  }
}
