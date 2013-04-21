package controllers

import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.libs.Crypto
import play.libs.Time

object Auth extends Controller {

  val REMEMBER_COOKIE = "rememberme"

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text,
      "remember" -> boolean
    ) verifying ("Invalid email or password", result => result match {
      case (email, password, remember) => check(email, password)
      case _ => {
        println(result.toString())
        false
      }

    })
  )

  def check(username: String, password: String) = {
    (username == "admin@admin" && password == "1234")
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        val action = (request.session.get("uri") match {
          case Some(uri) => Redirect(uri)
          case None => Redirect(controllers.routes.Application.index())
        }).withSession(
          Security.username -> Crypto.sign(user._1))

        if (user._3) {
          action.withCookies(Cookie(
            name = REMEMBER_COOKIE,
            value = Crypto.sign(user._1),
            maxAge = Some(Time.parseDuration("30d"))
          ))
        } else {
          action
        }
      }
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login()).withNewSession.discardingCookies(DiscardingCookie(REMEMBER_COOKIE)).flashing(
      "success" -> "You are now logged out."
    )
  }
}
