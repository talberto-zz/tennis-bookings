package models

import scala.language.implicitConversions // remove implicit conversion warnings
import play.api.db._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

/**
 * @author tomas
 */
private class Bookings(tag: Tag) extends Table[Booking](tag, "bookings") {
  /**
   * Column converters for use with the type Booking
   */
  object Converters {
    implicit val DateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
        { dateTime => new Timestamp(dateTime.getMillis) },
        { timestamp => new DateTime(timestamp) }
      )
    
    implicit val requestStatusColumnType = MappedColumnType.base[Booking.Status.Status, Int](
        { status => status.id },
        { integer => Booking.Status(integer) }
      )  
  }
  
  import Converters._
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def status = column[Booking.Status.Status]("status")
  def * = (id.?, date, status) <> ((Booking.apply _).tupled, Booking.unapply)
}

/**
 * Repository for Booking
 */
object BookingsRepository {
  /**
   * Holds the session for use in the repository
   */
  object Sandbox {
    val ds = DB.getDataSource()
    implicit def session = Database.forDataSource(ds).createSession
  }
  
  import Sandbox.session
    
  private val bookings = TableQuery[Bookings]
  
  private val requestById = Compiled((id: ConstColumn[Long]) => bookings filter(_.id === id))

  def findAll: Seq[Booking] = bookings.list
  
  def findById(id: Long): Option[Booking] = requestById(id).firstOption
  
  def insert(booking: Booking): Booking = {
    val id = (bookings returning bookings.map(_.id)) += booking
    Booking(Some(id), booking.date, booking.status)
  }
  
  def update(booking: Booking) = {
    bookings.filter(_.id === booking.id).map(b => (b.date, b.status)).update(booking.date, booking.status)
  }
  
  def delete(id: Long) = {
    bookings.filter(_.id === id).delete
  }
  
  def delete(booking: Booking): Unit = {
    delete(booking.id.get)
  }
}