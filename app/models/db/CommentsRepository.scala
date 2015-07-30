package models.db

import javax.inject.Singleton

import models.AppConfiguration._
import models.db.Sandbox.db
import models.db.SlickConverters._
import org.joda.time.DateTime
import play.api.Logger
import slick.driver.PostgresDriver.api._

import scala.concurrent._
import scala.concurrent.duration._

class Comments(tag: Tag) extends Table[Comment](tag, "comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def creationDate = column[DateTime]("creationDate")
  def text = column[String]("text")
  def screenshot = column[Option[String]]("screenshot")
  def bookingId = column[Long]("bookingId")
  def * = (id, creationDate, text, screenshot, bookingId) <> ((Comment.apply _).tupled, Comment.unapply)
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
    val query = comments.sortBy(_.creationDate.asc)
    val action = query.result
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def find(id: Long): Option[Comment] = {
    logger.trace(s"find(${id})")
    val query = comments.filter(_.id === id)
    val action = query.result
    val result = db.run(action)
    Await.result(result, Duration.Inf).headOption
  }
  
  def findByBookingId(bookingId: Long): Seq[Comment] = {
    logger.trace(s"findByBookingId(${bookingId})")
    val query = comments.filter(_.bookingId === bookingId).sortBy(_.creationDate.asc)
    val action = query.result
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def save(comment: Comment): Comment = {
    logger.trace(s"save(${comment})")
    val action = (comments returning comments.map(_.id) into ((c, id) => (c.copy(id=id)))) += comment
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def update(comment: Comment) = {
    logger.trace(s"update(${comment})")
    val query = comments.filter(_.id === comment.id)
    val action = query.update(comment)
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete(${id})")
    val query = comments.filter(_.id === id)
    val action = query.delete
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def addCommentToBooking(bookingId: Long, text: String, screenshot: Option[String] = None) = {
    logger.trace(s"addCommentToBooking(${bookingId}, ${text}, ${screenshot})")
    val comment = Comment(null.asInstanceOf[Long], DateTime.now(ParisTimeZone), text, screenshot, bookingId)
    save(comment)
  }
}
