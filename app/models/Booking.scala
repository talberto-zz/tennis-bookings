package models

import java.sql.Timestamp

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.DateTime

import scala.language.implicitConversions // remove implicit conversion warnings

case class Booking(id: Long = null.asInstanceOf[Long], dateTime: DateTime, court: Int, status: Booking.Status.Status = Booking.Status.PENDING) {
  val date = dateTime.toLocalDate()
  val time = dateTime.toLocalTime()
}

object Booking {
  
  def fromDateAndTime(id: Long, date: LocalDate, time: LocalTime, court: Int, status: Booking.Status.Status): Booking = Booking(id, date.toDateTime(time), court, status)
  
  /**
   * Status for a Booking
   */
  object Status extends Enumeration {
    type Status = Value
    val PENDING, RESERVED = Value
    
    def unapply(v: Int) = Status(v)
  }
}
