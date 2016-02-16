package models

import java.time.ZoneId

import org.joda.time.format.DateTimeFormat

import scala.concurrent.duration._

/**
 * Global app configuration
 */
object AppConfiguration {
  val ParisTimeZone = ZoneId.of("Europe/Paris")
  val DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  val DateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val TimeFormatter = DateTimeFormat.forPattern("HH:mm:ss")
  val HourPattern = "HH:mm"
  val HourFormatter = DateTimeFormat.forPattern("HH:mm")
  val RequestTimeoutDuration = 5 seconds
}