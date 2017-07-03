package tech.trodriguez.tennisbookings.back

import java.time.ZonedDateTime
import java.util.UUID

import play.api.libs.json.Json

object Reservation {
  implicit val JsonFormat = Json.format[Reservation]
}

case class Reservation(
                        id: UUID,
                        dateTime: ZonedDateTime,
                        court: Int
                      )
