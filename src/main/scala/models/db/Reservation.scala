package models.db

import java.time.ZonedDateTime

import models.EnumUtils
import play.api.libs.json.Json

import scala.language.implicitConversions

case class ReservationRequest(
                               dateTime: ZonedDateTime,
                               court: Int
                             )

object ReservationRequest {
  implicit val JsonFormat = Json.format[ReservationRequest]
}

case class Reservation(
                        id: Long,
                        creationDate: ZonedDateTime,
                        lastModified: ZonedDateTime,
                        dateTime: ZonedDateTime,
                        court: Int,
                        status: Reservation.Status.Status
                      )

object Reservation {

  /**
    * Status for a Reservation
    */
  object Status extends Enumeration {
    type Status = Value
    val
    NEW,
    SUBMITTED,
    SCHEDULED,
    FAILED,
    ALREADY_BOOKED,
    BOOKINGS_LIMIT_REACHED,
    SUCCESSFULLY_BOOKED = Value

    def unapply(v: Int) = Status(v)
  }

  implicit val StatusFormat = EnumUtils.enumFormat(Status)

  implicit val JsonFormat = Json.format[Reservation]
}
