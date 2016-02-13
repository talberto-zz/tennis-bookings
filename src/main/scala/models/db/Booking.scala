package models.db

import java.time.ZonedDateTime

import models.AppConfiguration._
import play.api.libs.json.Json

import scala.language.implicitConversions // remove implicit conversion warnings

case class ReservationRequest(dateTime: ZonedDateTime, court: Int)

object ReservationRequest {
  implicit val ReservationRequestJsonFormat = Json.format[ReservationRequest]
}

case class Booking(id: Long = null.asInstanceOf[Long], creationDate: org.joda.time.DateTime= org.joda.time.DateTime.now(), lastModified: org.joda.time.DateTime= org.joda.time.DateTime.now(), dateTime: org.joda.time.DateTime, court: Int, status: Booking.Status.Status = Booking.Status.SUBMITTED) {
  val date = dateTime.toLocalDate()
  val time = dateTime.toLocalTime()
}

object Booking {

  def fromDateAndTime(id: Long, creationDate: org.joda.time.DateTime, lastModified: org.joda.time.DateTime, date: org.joda.time.LocalDate, time: org.joda.time.LocalTime, court: Int, status: Booking.Status.Status): Booking = Booking(id, creationDate, lastModified, date.toDateTime(time, ParisTimeZone), court, status)

  def unapplyDateAndTime(booking: Booking): Option[(Long, org.joda.time.DateTime, org.joda.time.DateTime, org.joda.time.LocalDate, org.joda.time.LocalTime, Int, Booking.Status.Value)] = {
    Some(booking.id, booking.creationDate, booking.lastModified, booking.date, booking.time, booking.court, booking.status)
  }
  
  /**
   * Status for a Booking
   */
  object Status extends Enumeration {
    type Status = Value
    val 
      SUBMITTED, 
      SCHEDULED, 
      FAILED, 
      ALREADY_BOOKED, 
      BOOKINGS_LIMIT_REACHED,
      SUCCESSFULLY_BOOKED = Value
    
    def unapply(v: Int) = Status(v)
  }
}
