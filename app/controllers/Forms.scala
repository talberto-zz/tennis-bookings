package controllers

import controllers.Formats._

import play.api.data.Mapping
import play.api.data.Forms._

object Forms {

  def jodaLocalTime(pattern: String, timeZone: org.joda.time.DateTimeZone = org.joda.time.DateTimeZone.getDefault): Mapping[org.joda.time.LocalTime] = of[org.joda.time.LocalTime] as jodaLocalTimeFormat(pattern, timeZone)
    
  val jodaLocalTime = of[org.joda.time.LocalTime]
}
