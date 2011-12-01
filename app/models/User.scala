package models

import play.api.db._
import play.api.Play.current
import models._
import anorm._
import anorm.SqlParser._

case class User(id: Long,username: String, email:String,password: String)

object User {
  
  // -- Parsers
  
  
  val simple = {
    get[Long]("profile.id") ~/
    get[String]("profile.username") ~/
    get[String]("profile.email") ^^ {
      case id~username~email => User(id, username, email,"")
    }
  }
  
  def findByUsername(username:String): Option[User] = {
  	DB.withConnection { implicit connection =>
      SQL("select id,username,email from profile where username = {username}").on(
        'username -> username
      ).as(User.simple ?)
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(username: String, password: String): Option[User] = {
    
	Some(User(100,"koevet", "email","zombie"))
  }
   
  
  
}