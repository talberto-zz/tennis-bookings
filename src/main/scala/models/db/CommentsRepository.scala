package models.db

import models.AppConfiguration
import models.db.SlickConverters._
import org.joda.time.{DateTimeZone, DateTime}
import play.api.Logger
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def creationDate = column[DateTime]("creationDate")
  def text = column[String]("text")
  def screenshot = column[Option[String]]("screenshot")
  def bookingId = column[Long]("bookingId")
  def * = (id, creationDate, text, screenshot, bookingId) <> ((Comment.apply _).tupled, Comment.unapply)
  def booking = foreignKey("booking", bookingId, Queries.reservations)(_.id)
}

object CommentsRepository {
  val logger: Logger = Logger(this.getClass)
  val db = Sandbox.db

  private val comments = Queries.comments
  
  /**
   * Retrieve all the T's
   */
  def list: Future[Seq[Comment]] = {
    logger.trace("list()")
    val query = comments.sortBy(_.creationDate.asc)
    val action = query.result
    db.run(action)
  }
  
  def find(id: Long): Future[Option[Comment]] = {
    logger.trace(s"find(${id})")
    val query = comments.filter(_.id === id)
    val action = query.result.headOption
    db.run(action)
  }

  def findByBookingId(bookingId: Long): Future[Seq[Comment]] = {
    logger.trace(s"findByBookingId(${bookingId})")
    val query = comments.filter(_.bookingId === bookingId).sortBy(_.creationDate.asc)
    val action = query.result
    db.run(action)
  }
  
  def save(comment: Comment): Future[Comment] = {
    logger.trace(s"save(${comment})")
    val action = (comments returning comments.map(_.id) into ((c, id) => (c.copy(id=id)))) += comment
    db.run(action)
  }
  
  def update(comment: Comment): Future[Int] = {
    logger.trace(s"update(${comment})")
    val query = comments.filter(_.id === comment.id)
    val action = query.update(comment)
    db.run(action)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete(${id})")
    val query = comments.filter(_.id === id)
    val action = query.delete
    db.run(action)
  }
  
  def addCommentToBooking(bookingId: Long, text: String, screenshot: Option[String] = None) = {
    logger.trace(s"addCommentToBooking(${bookingId}, ${text}, ${screenshot})")
    val comment = Comment(null.asInstanceOf[Long], DateTime.now(DateTimeZone.forID(AppConfiguration.ParisTimeZone.getId)), text, screenshot, bookingId)
    save(comment)
  }
}
