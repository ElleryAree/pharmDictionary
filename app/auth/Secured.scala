package auth

import play.api.mvc._
import controllers.routes
import models.User
import play.Logger
import play.libs.Crypto

trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = {
    Logger.info("unauthorized");
    Results.Redirect(routes.Auth.login())
  }

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
  def withUser(f: User => (Request[AnyContent], User) => Result) = withAuth { name => implicit request =>
    Logger.info("WithUser: " + name)
    User.findOneByUsername(name).map { user =>
      f(user)(request, user)
    }.getOrElse(onUnauthorized(request))
  }
}
