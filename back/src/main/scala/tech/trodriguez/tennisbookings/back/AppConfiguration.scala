package tech.trodriguez.tennisbookings.back

import java.time.ZoneId
import java.time.format.{DateTimeFormatter => DateTimeFormat}

import scala.concurrent.duration._

/**
  * Global app configuration
  */
object AppConfiguration {
  val ParisTimeZone = ZoneId.of("Europe/Paris")
  val DateTimeFormatter = DateTimeFormat.ofPattern("yyyy-MM-dd HH:mm:ss")
  val DateFormatter = DateTimeFormat.ofPattern("yyyy-MM-dd")
  val TimeFormatter = DateTimeFormat.ofPattern("HH:mm:ss")
  val HourPattern = "HH:mm"
  val HourFormatter = DateTimeFormat.ofPattern("HH:mm")
  val RequestTimeoutDuration = 5 seconds
}
