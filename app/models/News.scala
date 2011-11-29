package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class News(id: Pk[Long], title: String, link: String, user: Long, points:Long)

object News {
    
    
  val simple = {
    get[Pk[Long]]("id") ~/
    get[String]("title") ~/
    get[String]("story") ~/
    get[Long]("profileId") ~/
    get[Int]("votes") ^^ {
      case id~title~link~user~points => News(
        id, title, link, user, points
      )
    }
  }
    
    def list(top: Int): Seq[News] = {
        DB.withConnection { implicit connection =>
      SQL(
        """
          select * from story 
          order by rank
        """
      ).as(News.simple *)
    }
    }
    
    def create(news: News): News = {
        DB.withConnection { implicit connection =>
    
        // Get the task id
          val id: Long = news.id.getOrElse {
            SQL("select next value for news_seq").as(scalar[Long])
          }
          
          SQL(
            """
              insert into story values (
                {id}, {title}, {story}, {profileId}, {created}, {votes}, {rank}
              )
            """
          ).on(
            'id -> id,
            'title -> news.title,
            'story -> news.link,
            'profileId -> news.user,
            'created -> new java.util.Date(),
            'votes -> 0,
            'rank -> 0
            
          ).executeUpdate()
          
          news.copy(id = Id(id))
        }
    }
    
    //def list
    
    
    
}