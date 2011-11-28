package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class News(id: Pk[Long], title: String, link: String, user: Long, points:Long)

object News {

    def create(news: News): News = {
        DB.withConnection { implicit connection =>
    
        // Get the task id
          val id: Long = news.id.getOrElse {
            SQL("select next value for news_seq").as(scalar[Long])
          }
          
          SQL(
            """
              insert into story values (
                {id}, {title}, {story}, {profileId}, {created}, {votes}
              )
            """
          ).on(
            'id -> id,
            'title -> news.title,
            'story -> news.link,
            'profileId -> news.user,
            'created -> new java.util.Date(),
            'votes -> 0
            
          ).executeUpdate()
          
          news.copy(id = Id(id))
        
            
            
        }
    }
    
}