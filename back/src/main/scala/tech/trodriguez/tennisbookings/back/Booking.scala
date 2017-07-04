package tech.trodriguez.tennisbookings.back

import java.time.ZonedDateTime
import java.util.UUID

import play.api.libs.json.Json

object Booking {
  implicit val JsonFormat = Json.format[Booking]
}

case class Booking(
                    id: UUID,
                    dateTime: ZonedDateTime,
                    court: Int
                  )
