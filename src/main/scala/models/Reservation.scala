package models

import java.time.ZonedDateTime
import java.util.UUID

case class Reservation(
                        id: UUID,
                        dateTime: ZonedDateTime,
                        court: Int
                      )
