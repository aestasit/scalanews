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
      "username"->ignored(""),
      "points" -> ignored(1),
      "comments" -> ignored(0),
      "created" -> ignored(new java.util.Date())
    )
  )

  def submit = IsAuthenticated { username => implicit request =>
    Ok(views.html.submit(newsForm))
  }
  
  def submitNews = IsAuthenticated { username => implicit request =>
      newsForm.bindFromRequest.fold (
        errors => BadRequest,
        news =>  {
              // Add the news!
              News.create(
                News(NotAssigned, news.title, news.link, 1001,"", 0,0,new java.util.Date())
              )
              Redirect("/news")
          }
      )
  }
  
  def voteNews(id: Long) = IsAuthenticated  { username => implicit request =>
      val voter = User.findByUsername(username)
      voter match {
          case None => Forbidden
          case Some(u) => {
              News++(id,u.id)
              val voted = request.session.get("voted_stories").getOrElse("")
              Ok("").withSession(request.session.+("voted_stories", id+","+voted))
          }
    }
  }
  
}