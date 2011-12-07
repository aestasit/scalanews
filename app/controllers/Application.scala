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
  
  val signupForm = Form (
    of(
      "username" -> (text verifying ("dio gane",{ !User.findByUsername(_).isDefined })),
      "password" -> text,
      "email" -> text
    ) 
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
  
  def signup = Action { implicit request =>
      Ok(views.html.signup(signupForm))
  }
  
  def createAccount = Action { implicit request =>
    signupForm.bindFromRequest.fold (
        formWithErrors => BadRequest(views.html.signup(formWithErrors)),
        {
          case (username, password, email) => 
            User.create(email, username, md5(password))
            Redirect(routes.Application.news).withSession (
              "username" -> username).flashing("success" -> "You are signed up!"
            )
        }
        
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
    Logger("play").error("vote news")
    Ok(views.html.news(News.list(10)))
  }
  
  def md5(s: String) = {
    
    val m = MessageDigest.getInstance("SHA"); 
    m.update("I58'_6d>O2238K*='2&*@@".getBytes("UTF8")); 
    m.update(s.getBytes("UTF8")); 
    m.digest().map(0xFF & _).map { "%02x".format(_) }.mkString
   
  }
  
  def viewNewsComments(id:Long) = Action { implicit request =>
    val story = News.findById(id)
    Ok(views.html.comments(story.get,Comment.listByStoryId(id),commentForm))
  }
  
  val commentForm = Form(
      of(Comment.apply _,Comment.unapply _)(
        "id" ->  ignored(NotAssigned),
        "comment" -> requiredText,
        "profileId" -> ignored(0),
        "username" -> ignored(""),
        "storyId" -> ignored(0),
        "parent" -> ignored(Option(0))
      )
    )

  def commentNews(id:Long) = Action {implicit request =>
     val story = News.findById(id)
     commentForm.bindFromRequest.fold (
       errors => {
         BadRequest(views.html.comments(story.get,Comment.listByStoryId(id),errors))
       },
       comment => {
         val commenter = User.findByUsername(request.session.get("username").get)
         commenter match {
         case None => Forbidden
         case Some(u) => {
              Comment.create(
                Comment(NotAssigned,comment.comment,u.id,u.username,id,null));
              Ok(views.html.news(News.list(10)))
          }
        }
       }
     )

  }
}
  

 /**
  * Provide security features
  */
trait Secured {

  /**
   * Retrieve the connected user username.
   */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  
}
  
  