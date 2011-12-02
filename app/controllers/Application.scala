package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import play.api.data.validation.Constraints._
import anorm._
import java.security.MessageDigest

object Application extends Controller {
  
  val loginForm = Form (
    of(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (username, password) => User.authenticate(username, md5(password)).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }
  
  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.news).withSession("username" -> user._1)
    )
  }
  
  def logout = Action { implicit request =>
    Redirect(routes.Application.news).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
  
  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def news = Action { implicit request =>
    Ok(views.html.news(News.list(10)))
  }
  
  def md5(s: String) = {
    
    val m = MessageDigest.getInstance("SHA"); 
    m.update("I58'_6d>O2238K*='2&*@@".getBytes("UTF8")); 
    m.update(s.getBytes("UTF8")); 
    val u = m.digest().map(0xFF & _).map { "%02x".format(_) }.mkString
    println( u)
    u
  }
  
  
  
}
 /**
  * Provide security features
  */
trait Secured extends Security.AllAuthenticated {

  /**
   * Retrieve the connected user email.
   */
  override def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the use in not authorized.
   */
  override def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

}
  
  