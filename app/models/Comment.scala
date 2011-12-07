package models
import play.api.Play.current
import models._
import anorm._
import anorm.SqlParser._
import play.api.db._
case class Comment(id: Pk[Long],comment:String,profileId:Long,username:String,storyId:Long,parent:Option[Long])

object Comment {

	val simple = {
		get[Pk[Long]]("comments.id") ~/
		get[String]("comments.comment") ~/
		get[Long]("comments.profileId") ~/
		get[String]("profile.username") ~/
		get[Long]("comments.storyId") ~/
		get[Option[Long]]("comments.parentComment")^^{
			case id~comment~profileId~username~storyId~parent => Comment(id,comment,profileId,username,storyId,parent)
		}
	}
	
	def listByStoryId(storyId:Long): Seq[Comment] = {
		DB.withConnection { implicit connection =>
			SQL(
			   """
			     select comments.id,comments.comment,comments.profileId,profile.username,comments.storyId,comments.parentComment from comments left join profile profile on comments.profileId = profile.id
			   """
			   ).as(Comment.simple *)
		}
	
	} 

	 def create(comment: Comment): Comment = {
        DB.withConnection { implicit connection =>
    
        // Get the task id
          val id: Long = comment.id.getOrElse {
            SQL("select next value for comments_seq").as(scalar[Long])
          }
          SQL(
            """
              insert into comments(id,comment,profileId,storyId,created) values (
                {id}, {comment}, {profileId}, {storyId}, {created}
              )
            """
          ).on(
            'id -> id,
            'comment -> comment.comment,
            'profileId -> comment.profileId,
            'storyId -> comment.storyId,
            'created -> new java.util.Date()
          ).executeUpdate()
          
          comment.copy(id = Id(id))
        }
    }


}
