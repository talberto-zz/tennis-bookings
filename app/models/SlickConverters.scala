package models

import models.AppConfiguration._

import org.joda.time.DateTime

import play.api.db._
import play.api.Play.current
import play.api.Logger

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._

object SlickConverters {
  implicit val DateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
        { dateTime => new Timestamp(dateTime.getMillis) },
        { timestamp => new DateTime(timestamp, ParisTimeZone) }
      )
}
