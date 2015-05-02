package models.db

import javax.inject.Singleton

import models.AppConfiguration._
import models.db.Sandbox.db
import models.db.SlickConverters._
import org.joda.time.DateTime
import play.api.Logger

import scala.language.implicitConversions
import scala.slick.driver.PostgresDriver.simple._

class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def creationDate = column[DateTime]("creationDate")
  def text = column[String]("text")
  def bookingId = column[Long]("bookingId")
  def * = (id, creationDate, text, bookingId) <> ((Comment.apply _).tupled, Comment.unapply)
  def booking = foreignKey("booking", bookingId, Queries.bookings)(_.id)
}

@Singleton
class CommentsRepository extends Repository[Comment] {
  val logger: Logger = Logger(this.getClass)
  
  private val comments = Queries.comments
  
  /**
   * Retrieve all the T's
   */
  def list: Seq[Comment] = {
    logger.trace("list()")
    db.withSession { implicit session => 
      comments.sortBy(_.creationDate.asc).list
    }
  }
  
  def find(id: Long): Option[Comment] = {
    logger.trace(s"find(${id})")
    db.withSession { implicit session => 
      comments.filter(_.id === id).firstOption
    }
  }
  
  def findByBookingId(bookingId: Long): Seq[Comment] = {
    logger.trace(s"findByBookingId(${bookingId})")
    db.withSession { implicit session => 
      comments.filter(_.bookingId === bookingId).sortBy(_.creationDate.asc).list
    }
  }
  
  def save(comment: Comment): Comment = {
    logger.trace(s"save(${comment})")
    db.withSession { implicit session => 
      (comments returning comments.map(_.id) into ((c, id) => (c.copy(id=id)))) += comment
    }
  }
  
  def update(comment: Comment) = {
    logger.trace(s"update(${comment})")
    db.withSession { implicit session => 
      comments.filter(_.id === comment.id).update(comment)
    }
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete(${id})")
    db.withSession { implicit session => 
      comments.filter(_.id === id).delete
    }
  }
  
  def addCommentToBooking(bookingId: Long, text: String) = {
    logger.trace(s"addCommentToBooking(${bookingId}, ${text})")
    val comment = Comment(null.asInstanceOf[Long], DateTime.now(ParisTimeZone), text, bookingId)
    save(comment)
  }
}
