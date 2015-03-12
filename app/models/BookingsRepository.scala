package models

import models.SlickConverters._
import models.Sandbox.session

import org.joda.time.DateTime

import play.api.db._
import play.api.Play.current
import play.api.Logger

import java.sql.Timestamp

import scala.language.implicitConversions // remove implicit conversion warnings
import scala.slick.driver.PostgresDriver.simple._

/**
 * @author tomas
 */
class Bookings(tag: Tag) extends Table[Booking](tag, "bookings") {
  /**
   * Column converters for use with the type Booking
   */
  object Converters {
    implicit val requestStatusColumnType = MappedColumnType.base[Booking.Status.Status, Int](
        { status => status.id },
        { integer => Booking.Status(integer) }
      )  
  }
  
  import Converters._
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def creationDate = column[DateTime]("creationDate")
  def lastModified = column[DateTime]("lastModified")
  def dateTime = column[DateTime]("dateTime")
  def court = column[Int]("court")
  def status = column[Booking.Status.Status]("status")
  def * = (id, creationDate, lastModified, dateTime, court, status) <> ((Booking.apply _).tupled, Booking.unapply)
}

/**
 * Repository for Booking
 */
class BookingsRepository extends Repository[Booking] {  
  val logger: Logger = Logger(this.getClass)
  
  private val bookings = Queries.bookings

  /**
   * Retrieve all the Booking's
   */
  def list: Seq[Booking] = {
    logger.trace("list()")
    bookings.sortBy(_.creationDate.asc).list
  }
  
  def find(id: Long): Option[Booking] = {
    logger.trace(s"findById($id)")
    bookings filter(_.id === id) firstOption
  }
  
  def save(booking: Booking): Booking = {
    logger.trace(s"save($booking)")
    (bookings returning bookings.map(_.id) into ((b, id) => (b.copy(id=id)))) += booking
  }
  
  def update(booking: Booking) = {
    logger.trace(s"update($booking)")
    bookings.filter(_.id === booking.id).map(b => (b.dateTime, b.status)).update(booking.dateTime, booking.status)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete($id)")
    bookings.filter(_.id === id).delete
  }
}

object BookingsRepository {
  def apply() = new BookingsRepository
}