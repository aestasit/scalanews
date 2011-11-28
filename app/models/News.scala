package models

import play.api.db._
import anorm._
import anorm.SqlParser._

case class News(id: Pk[Long], title: String, link: String, user: String, points:Long)

object News {


}