package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class News(id: Pk[Long], title: String, link: String, user:Long,username:String, points:Long,comments:Long,created:java.util.Date)

object News {
    

    
  val simple = {
    get[Pk[Long]]("id") ~/
    get[String]("title") ~/
    get[String]("story") ~/
    get[Long]("profileId") ~/
    get[String]("username") ~/
    get[Int]("votes") ~/
    get[Long]("comments") ~/
    get[java.util.Date]("created") ^^ {
      case id~title~link~user~username~points~comments~created => News(
        id, title, link, user,username, points,comments,created
      )
    }
  }

  def findById(id:Long) : Option[News] = {
     DB.withConnection { implicit connection =>
      SQL("""
            select s.id,s.title,s.story,s.profileId,p.username,s.votes,
            (select count(*) from comments where storyId = s.id)as comments,s.created from story s left join profile p on s.profileId = p.id 
            where s.id = {id}""")
            .on(
        'id -> id
      ).as(News.simple ?)
    }
  }
  
  
  def userVote(userid:Long, newsid:Long) = {
    DB.withConnection { implicit connection =>
        // Get the task id
        val id: Long = SQL("select next value for votes_seq").as(scalar[Long])
        
        SQL(
        """
          insert into vote (id, storyId, created, profileId) values (
            {id}, {storyId}, NOW(), {profileId}
          )
        """
        ).on(
            'id -> id,
            'storyId -> newsid,
            'profileId -> userid
        ).executeUpdate()
    }
  }
  
  def newsVote(id:Long) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update story set votes = votes +1 where id = {id}
        """
      ).on(
        'id -> id
      ).executeUpdate()
      //ADD support for vote table
    }
    
  }
  
  
  def ++(id:Long, profileId:Long) = {
    newsVote(id)
    userVote(profileId,id)
  }
  
  def list(top: Int): Seq[News] = {
    DB.withConnection { implicit connection =>
        SQL(
            """
              select s.id,s.title,s.story,s.profileId,p.username,s.votes,(select count(*) from comments where storyId = s.id)as comments,s.created from story s left join profile p on s.profileId = p.id  
              order by s.rank
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
                {id}, {title}, {story}, {profileId},NOW(), {votes}, {rank}
              )
            """
          ).on(
            'id -> id,
            'title -> news.title,
            'story -> news.link,
            'profileId -> news.user,
            'votes -> 0,
            'rank -> 0
            
          ).executeUpdate()
          
          news.copy(id = Id(id))
        }
    }
    
    
    def getVotedNewsByUser(profileId:Long, max:Int): Seq[String] = {
      DB.withConnection { implicit connection =>
        val votedNews = SQL(
          """
            select storyId from vote where profileId = {id} order by created desc
          """
          ).on('id -> profileId)
        votedNews().map (row => row[Long]("storyId").toString ).toList
        
      }

    }    
    
    
}