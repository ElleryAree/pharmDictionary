package models

case class User (name: String, admin: Boolean){
}

object User{
  def findOneByUsername(name: String): Option[User] = {
    name match {
      case "admin@admin" => Some(User("admin", admin = true))
      case "some@some" =>  Some(User("some", admin = false))
      case _ => None
    }
  }

}
