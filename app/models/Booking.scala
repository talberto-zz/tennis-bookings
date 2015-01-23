package models

import scala.language.implicitConversions // remove implicit conversion warnings
import org.joda.time.DateTime
import java.sql.Timestamp

class Booking private(val id: Option[Long], val dateTime: DateTime, val status: Booking.Status.Status)

object Booking {
  /**
   * Implicits for Booking
   */
  object Implicits {
    implicit def millisToDateTime(milis: Long): DateTime = new DateTime(milis)
    
    implicit def dateTimeToMillis(dateTime: DateTime): Long = dateTime.getMillis
    
    implicit def intToStatus(intStatus: Int): Booking.Status.Status = Booking.Status(intStatus)
    
    implicit def statusToInt(status: Booking.Status.Status): Int = status.id
    
    implicit def bookingRawApplyToApply(f: (Option[Long], Long, Int) => Booking): (Option[Long], DateTime, Booking.Status.Status) => Booking = (id: Option[Long], dateTime: DateTime, status: Booking.Status.Status) => f(id, dateTime, status)
    
    implicit def bookingApplyToRawApply(f: (Option[Long], DateTime, Booking.Status.Status) => Booking): (Option[Long], Long, Int) => Booking = (id: Option[Long], dateTime: Long, status: Int) => f(id, dateTime, status)
    
    implicit def bookingUnapplyToRawUnapply(f: (Booking) => Option[(Option[Long], DateTime, Booking.Status.Status)]): (Booking) => Option[(Option[Long], Long, Int)] = {
      (req: Booking) => {
        val option = f(req)
        option match {
          case Some((id, dateTime, status)) => Some((id, dateTime, status))
          case _ => None
        }
      }
    }
    
    implicit def bookingOptToBookingRawOpt(opt: Option[(Option[Long], DateTime, Booking.Status.Status)]): Option[(Option[Long], Long, Int)] = {
      opt match {
        case Some((id, dateTime, status)) => Some((id, dateTime, status))
        case _ => None
      }
    }
  }
  
  /**
   * Status for a Booking
   */
  object Status extends Enumeration {
    type Status = Value
    val PENDING, RESERVED = Value
    
    def unapply(v: Int) = Status(v)
  }
  
  def apply(id: Option[Long] = None, dateTime: DateTime, status: Status.Status): Booking = new Booking(id, dateTime, status)
  
  def unapply(booking: Booking): Option[(Option[Long], DateTime, Status.Status)] = Option(booking.id, booking.dateTime, booking.status)
}
