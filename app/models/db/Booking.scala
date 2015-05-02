package models.db

import models.AppConfiguration._
import org.joda.time.{DateTime, LocalDate, LocalTime}

import scala.language.implicitConversions // remove implicit conversion warnings

case class Booking(id: Long = null.asInstanceOf[Long], creationDate: DateTime = DateTime.now(), lastModified: DateTime = DateTime.now(), dateTime: DateTime, court: Int, status: Booking.Status.Status = Booking.Status.SUBMITTED) {
  val date = dateTime.toLocalDate()
  val time = dateTime.toLocalTime()
}

object Booking {
  
  def fromDateAndTime(id: Long, creationDate: DateTime, lastModified: DateTime, date: LocalDate, time: LocalTime, court: Int, status: Booking.Status.Status): Booking = Booking(id, creationDate, lastModified, date.toDateTime(time, ParisTimeZone), court, status)
  
  def unapplyDateAndTime(booking: Booking): Option[(Long, DateTime, DateTime, LocalDate, LocalTime, Int, Booking.Status.Value)] = {
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