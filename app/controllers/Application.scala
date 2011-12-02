package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import models._
import play.api.data.validation.Constraints._
import anorm._

object Application extends Controller {
  
  val loginForm = Form(
    of(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (username, password) => User.authenticate(username, password).isDefined
    })
  )

  
  val newsForm = Form(
    of(News.apply _)(
      "id" -> ignored(NotAssigned),
      "title" -> requiredText,
      "link" -> requiredText,
      "user" -> ignored(100),
      "points" -> ignored(1)
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
  
  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def news = Action { implicit request =>
    
    
    //val news: List[News] = List(News(null, "Aestas buys Microsoft", "http://www.aestasit.com", 100, 999))
    Ok(views.html.news(News.list(10)))

  }
  
  def logout = Action { implicit request =>
    Redirect(routes.Application.news).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
  
  def submit = Action { implicit request =>
    Ok(views.html.submit(newsForm))
  }
  
  def submitNews = Action { implicit request =>
      newsForm.bindFromRequest.fold (
      errors => BadRequest,
      news =>  {
            val news2: List[News] = List(News(null, news.title, news.link, 100, 0))
            // Add the news!
            News.create(
              News(NotAssigned, news.title, news.link, 1001, 0)
            )
            Redirect("/news")
        }
      
    )

  }
  
  def voteNews(id: Long) = Action { implicit request =>
      request.session.get("username") match {
        case None => Forbidden
      	case Some(username) =>{
      		val voter = User.findByUsername(username)
      		voter match {
      		 case None => Forbidden
      		 case Some(u) => {
      		 	News++(id,u.id)
      		 	Ok
      		 	}
      		 }
      	}
    }
  }
  
}
  
  