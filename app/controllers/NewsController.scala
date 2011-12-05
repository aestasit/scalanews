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
    of(News.apply _)(
      "id" -> ignored(NotAssigned),
      "title" -> requiredText,
      "link" -> requiredText,
      "user" -> ignored(100),
      "username"->ignored(""),
      "points" -> ignored(1)
    )
  )
  
  def submit = Action { implicit request =>
    Ok(views.html.submit(newsForm))
  }
  
  def submitNews = Action { implicit request =>
      newsForm.bindFromRequest.fold (
      errors => BadRequest,
      news =>  {
            // Add the news!
            News.create(
              News(NotAssigned, news.title, news.link, 1001,"", 0)
            )
            Redirect("/news")
        }
    )
  }
  
  def voteNews(id: Long) = Action { implicit request =>
    
    username(request) match {
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