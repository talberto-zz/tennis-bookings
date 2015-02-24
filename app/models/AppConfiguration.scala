package models

import org.joda.time.DateTimeZone
/**
 * Global app configuration
 */
object AppConfiguration {
  val ParisTimeZone: DateTimeZone = DateTimeZone.forID("Europe/Paris")
}