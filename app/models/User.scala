package models

import play.api.db._
import play.api.Play.current
import models._
import anorm._
import anorm.SqlParser._

case class User(username: String, password: String)

object User {
  
  // -- Parsers
  
  
  
  /**
   * Authenticate a User.
   */
  def authenticate(username: String, password: String): Option[User] = {
    
	Some(User("koevet", "zombie"))
  }
   
  
  
}