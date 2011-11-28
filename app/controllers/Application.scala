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
  
  def index = Action { 
    Ok(views.html.index("Your new application is ready."))
  }

  def news = Action { request =>
    val news: List[News] = List(News(null, "Aestas buys Microsoft", "http://www.aestasit.com", 100, 999))
    Ok(views.html.news(news, request.session.get("username").get))

  }
  
  def submit = Action {
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
            Ok(views.html.news(news2, request.session.get("username").get))
        }
      
    )

  }
  
}
  
  