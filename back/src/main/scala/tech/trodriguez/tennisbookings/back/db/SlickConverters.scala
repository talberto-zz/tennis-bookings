package tech.trodriguez.tennisbookings.back.db

import java.sql.Timestamp
import java.time.ZonedDateTime

import tech.trodriguez.tennisbookings.back.AppConfiguration._
import org.joda.time.{DateTimeZone, DateTime}
import slick.jdbc.PostgresProfile.api._

object SlickConverters {
  implicit val DateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
        { dateTime => new Timestamp(dateTime.getMillis) },
        { timestamp => new DateTime(timestamp, DateTimeZone.forID(ParisTimeZone.getId)) }
      )

  implicit val ZonedDateTimeColumnType = MappedColumnType.base[ZonedDateTime, Timestamp](
    { dateTime => Timestamp.from(dateTime.toInstant) },
    { timestamp => ZonedDateTime.ofInstant(timestamp.toInstant, ParisTimeZone) }
  )
}
