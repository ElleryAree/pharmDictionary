package controllers

import play.api.mvc._

import models.Group
import auth.Secured
import play.api.i18n.Lang
import play.api.libs.json._
import play.api.data.Form
import play.api.data.Forms._

object GroupsController extends Controller with Secured{

  def list()(implicit lang: Lang) = withUser {username => (request, user) =>
    Ok(Json.obj(
      "success" -> true,
      "data" -> Json.arr(Group.all())
    ))
  }

  def create = withUser {username => (request, user) =>
    processRequestWithMethod(Group.create)(request)
  }

  def edit(id: String) = withUser {username => (request, user) =>
    processRequestWithMethod(Group.save(id, _))(request)
  }

  def processRequestWithMethod(method: => (String) => Unit)(implicit request: Request[AnyContent]) = {
    taskForm.bindFromRequest.fold(
      errors => BadRequest(Json.obj(
        "success" -> false,
        "data" -> errors.toString)),
      data => {
        method(data)
        Ok(Json.obj(
          "success" -> true,
          "data" -> "None")
        )
      }
    )
  }

  val taskForm = Form(
    "name" -> nonEmptyText
  )

}
