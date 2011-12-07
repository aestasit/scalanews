package controllers

import play.api._
import play.api.mvc._
import play.api.data._

import anorm._

import models._
import views._


/**
 * Manage news related operations.
 */
object NewsController extends Controller with Secured {
    
  val newsForm = Form(
    of(News.apply _, News.unapply _)(
      "id" -> ignored(NotAssigned),
      "title" -> requiredText,
      "link" -> requiredText,
      "user" -> ignored(100),
      "points" -> ignored(1)
    )
  )
  
  def submit = Action { implicit request =>
    Ok(views.html.submit(newsForm))
  }
  
  def submitNews = IsAuthenticated { username => implicit request =>
      newsForm.bindFromRequest.fold (
      errors => BadRequest,
      news =>  {
            // Add the news!
            News.create(
              News(NotAssigned, news.title, news.link, 1001, 0)
            )
            Redirect("/news")
        }
    )
  }
  
  def voteNews(id: Long) = IsAuthenticated  { username => _ =>
    
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