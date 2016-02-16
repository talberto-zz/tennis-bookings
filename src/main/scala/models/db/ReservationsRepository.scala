package models.db

import java.time.ZonedDateTime

import play.api.Logger
import slick.driver.PostgresDriver.api._

import scala.concurrent._
import scala.language.implicitConversions

import models.db.SlickConverters._

/**
  * @author tomas
  */
class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {

  /**
    * Column converters for use with the type Reservation
    */
  object Converters {
    implicit val requestStatusColumnType = MappedColumnType.base[Reservation.Status.Status, Int](
      { status => status.id }, { integer => Reservation.Status(integer) }
    )
  }

  import Converters._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def creationDate = column[ZonedDateTime]("creationDate")

  def lastModified = column[ZonedDateTime]("lastModified")

  def dateTime = column[ZonedDateTime]("dateTime")

  def court = column[Int]("court")

  def status = column[Reservation.Status.Status]("status")

  def * = (id, creationDate, lastModified, dateTime, court, status) <>((Reservation.apply _).tupled, Reservation.unapply)
}


/**
  * Created by trodriguez on 16/02/16.
  */
/**
  * Repository for Booking
  */
object ReservationsRepository {

  val logger: Logger = Logger(this.getClass)
  val db = Sandbox.db

  private val reservations = Queries.reservations

  def create(req: ReservationRequest): Future[Reservation] = {
    logger.debug(s"Will create a new reservation from the request $req")
    val newReservation = Reservation(
      id = 0,
      creationDate = ZonedDateTime.now(),
      lastModified = ZonedDateTime.now(),
      dateTime = req.dateTime,
      court = req.court,
      status = Reservation.Status.NEW
    )
    val action = (reservations returning reservations.map(_.id) into ((b, id) => (b.copy(id = id)))) += newReservation
    db.run(action)
  }

//  /**
//    * Retrieve all the Booking's
//    */
//  def list: Future[Seq[Booking]] = {
//    logger.trace("list()")
//    val query = bookings.sortBy(_.creationDate.asc)
//    val action = query.result
//    db.run(action)
//  }
//
//  def find(id: Long): Future[Option[Booking]] = {
//    logger.trace(s"findById($id)")
//    val query = bookings filter(_.id === id)
//    val action = query.result.headOption
//    db.run(action)
//  }
//
//  def save(booking: Booking): Future[Booking] = {
//    logger.trace(s"save($booking)")
//    val action = ((bookings returning bookings.map(_.id) into ((b, id) => (b.copy(id=id)))) += booking)
//    db.run(action)
//  }
//
//  def update(booking: Booking): Future[Int] = {
//    logger.trace(s"update($booking)")
//    val query = bookings.filter(_.id === booking.id)
//    val action = query.update(booking)
//    db.run(action)
//  }
//
//  def delete(id: Long): Future[Int] = {
//    logger.trace(s"delete($id)")
//    val query = bookings.filter(_.id === id)
//    val action = query.delete
//    db.run(action)
//  }
}
