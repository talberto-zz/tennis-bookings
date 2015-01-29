package models

import scala.language.implicitConversions // remove implicit conversion warnings
import play.api.db._
import play.api.Play.current
import play.api.Logger
import scala.slick.driver.PostgresDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

/**
 * @author tomas
 */
class Bookings(tag: Tag) extends Table[Booking](tag, "bookings") {
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
  def dateTime = column[DateTime]("dateTime")
  def status = column[Booking.Status.Status]("status")
  def * = (id, dateTime, status) <> ((Booking.apply _).tupled, Booking.unapply)
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
  
  val logger: Logger = Logger(this.getClass)
  
  private val bookings = TableQuery[Bookings]
  
  private val requestById = Compiled((id: ConstColumn[Long]) => bookings filter(_.id === id))

  /**
   * Retrieve all the Booking's
   */
  def findAll: Seq[Booking] = {
    logger.trace("findAll()")
    bookings.list
  }
  
  /**
   * Finds a Booking given it's id
   */
  def findById2(id: Long): Option[Booking] = {
    logger.trace(s"findById2($id)")
    bookings filter(_.id === id) firstOption
  }
  
  def findById(id: Long): Option[Booking] = {
    logger.trace(s"findById($id)")
    val booking = findById2(id)
    logger.debug(s"findById($id) = $booking")
    booking
  }
  
  def create(dateTime: DateTime): Booking = {
    logger.trace(s"create($dateTime)")
    val booking = Booking(dateTime = dateTime)
    (bookings returning bookings.map(_.id) into ((b, id) => (b.copy(id=id)))) += booking
  }
  
  def update(id: Long, booking: Booking) = {
    logger.trace(s"update($id, $booking)")
    bookings.filter(_.id === booking.id).map(b => (b.dateTime, b.status)).update(booking.dateTime, booking.status)
  }
  
  def updateDateTime(id: Long, dateTime: DateTime) = {
    logger.trace(s"update($id, $dateTime)")
    bookings.filter(_.id === id).map(b => (b.dateTime)).update(dateTime)
  }
  
  def delete(id: Long) = {
    logger.trace(s"delete($id)")
    bookings.filter(_.id === id).delete
  }
}