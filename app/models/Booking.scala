package models

import scala.language.implicitConversions // remove implicit conversion warnings
import org.joda.time.DateTime
import java.sql.Timestamp

case class Booking(id: Long = null.asInstanceOf[Long], dateTime: DateTime, status: Booking.Status.Status = Booking.Status.PENDING)

object Booking {
  
  /**
   * Status for a Booking
   */
  object Status extends Enumeration {
    type Status = Value
    val PENDING, RESERVED = Value
    
    def unapply(v: Int) = Status(v)
  }
}
