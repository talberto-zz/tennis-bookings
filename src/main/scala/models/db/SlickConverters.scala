package models.db

import java.sql.Timestamp

import models.AppConfiguration._
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

object SlickConverters {
  implicit val DateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
        { dateTime => new Timestamp(dateTime.getMillis) },
        { timestamp => new DateTime(timestamp, ParisTimeZone) }
      )
}
