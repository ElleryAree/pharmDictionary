package models

case class User (name: String){
}

object User{
  def findOneByUsername(name: String): Option[User] = {
    name match {
      case "admin" => Some(User("admin"))
      case _ => None
    }
  }

}
