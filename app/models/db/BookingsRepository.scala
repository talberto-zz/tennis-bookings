package models.db

import javax.inject.Singleton

import models.db.Sandbox.db
import models.db.SlickConverters._
import org.joda.time.DateTime
import play.api.Logger
import slick.driver.PostgresDriver.api._

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.implicitConversions

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
@Singleton
class BookingsRepository extends Repository[Booking] {  
  val logger: Logger = Logger(this.getClass)
  
  private val bookings = Queries.bookings

  /**
   * Retrieve all the Booking's
   */
  def list: Seq[Booking] = {
    logger.trace("list()")
    val query = bookings.sortBy(_.creationDate.asc)
    val action = query.result
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def find(id: Long): Option[Booking] = {
    logger.trace(s"findById($id)")
    val query = bookings filter(_.id === id)
    val action = query.result
    val result = db.run(action)
    Await.result(result, Duration.Inf).headOption
  }
  
  def save(booking: Booking): Booking = {
    logger.trace(s"save($booking)")
    val action = ((bookings returning bookings.map(_.id) into ((b, id) => (b.copy(id=id)))) += booking)
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def update(booking: Booking) = {
    logger.trace(s"update($booking)")
    val query = bookings.filter(_.id === booking.id)
    val action = query.update(booking)
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete($id)")
    val query = bookings.filter(_.id === id)
    val action = query.delete
    val result = db.run(action)
    Await.result(result, Duration.Inf)
  }
}
