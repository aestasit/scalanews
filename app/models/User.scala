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
    
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from profile where 
         username = {username} and password = {password}
        """
      ).on(
        'username -> username,
        'password -> password
      ).as(User.simple ?)
    }
  }
  
  def create(email: String, username: String, password: String) = {
    DB.withConnection { implicit connection =>
      // Get the task id
      val id: Long = SQL("select next value for user_seq").as(scalar[Long])
      SQL(
        """
          insert into profile (id, username, password, created, email) values (
            {id}, {username}, {password}, {created}, {email}
          )
        """
        ).on(
            'id -> id,
            'email -> email,
            'username -> username,
            'password -> password,
            'created -> new java.util.Date()
        ).executeUpdate() 
    
    }
  }  
   
  
  
}