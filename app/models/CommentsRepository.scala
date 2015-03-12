package models

import SlickConverters._
import Sandbox.session

import models.AppConfiguration._

import org.joda.time.DateTime

import play.api.db._
import play.api.Play.current
import play.api.Logger

import java.sql.Timestamp

import scala.language.implicitConversions // remove implicit conversion warnings
import scala.slick.driver.PostgresDriver.simple._

object Queries {
  val bookings = TableQuery[Bookings]
  val comments = TableQuery[Comments]
}

class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def creationDate = column[DateTime]("creationDate")
  def text = column[String]("text")
  def bookingId = column[Long]("bookingId")
  def * = (id, creationDate, text, bookingId) <> ((Comment.apply _).tupled, Comment.unapply)
  def booking = foreignKey("booking", bookingId, Queries.bookings)(_.id)
}

class CommentsRepository extends Repository[Comment] {
  val logger: Logger = Logger(this.getClass)
  
  private val comments = Queries.comments
  
  /**
   * Retrieve all the T's
   */
  def list: Seq[Comment] = {
    logger.trace("list()")
    comments.sortBy(_.creationDate.asc).list
  }
  
  def find(id: Long): Option[Comment] = {
    logger.trace(s"find(${id}")
    comments.filter(_.id === id).firstOption
  }
  
  def findByBookingId(bookingId: Long): Seq[Comment] = {
    logger.trace(s"findByBookingId(${bookingId}")
    comments.filter(_.bookingId === bookingId).sortBy(_.creationDate.asc).list
  }
  
  def save(comment: Comment): Comment = {
    logger.trace(s"save(${comment}")
    (comments returning comments.map(_.id) into ((c, id) => (c.copy(id=id)))) += comment
  }
  
  def update(comment: Comment) = {
    logger.trace(s"update(${comment}")
    comments.filter(_.id === comment.id).update(comment)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete(${id}")
    comments.filter(_.id === id).delete
  }
  
  def addCommentToBooking(bookingId: Long, text: String) = {
    logger.trace(s"addCommentToBooking(${bookingId}, ${text})")
    val comment = Comment(null.asInstanceOf[Long], DateTime.now(ParisTimeZone), text, bookingId)
    save(comment)
  }
}

object CommentsRepository {
  def apply() = new CommentsRepository
}
