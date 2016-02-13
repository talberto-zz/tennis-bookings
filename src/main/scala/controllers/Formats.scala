package controllers

import org.joda.time.LocalTime
import org.joda.time.DateTimeZone

import play.api.data.format.Formatter
import play.api.data.format.Formats._
import play.api.data.FormError

import scala.Some

object Formats {
  /**
   * Formatter for the `org.joda.time.LocalTime` type.
   *
   * @param pattern a date pattern as specified in `org.joda.time.format.DateTimeFormat`.
   */
  def jodaLocalTimeFormat(pattern: String, timeZone: DateTimeZone = DateTimeZone.getDefault): Formatter[LocalTime] = new Formatter[LocalTime] {

    val formatter = org.joda.time.format.DateTimeFormat.forPattern(pattern).withZone(timeZone)
    
    override val format = Some(("format.date", Seq(pattern)))

    def bind(key: String, data: Map[String, String]) = {

      stringFormat.bind(key, data).right.flatMap { s =>
        scala.util.control.Exception.allCatch[LocalTime]
          .either(LocalTime.parse(s, formatter))
          .left.map(e => Seq(FormError(key, "error.time", Nil)))
      }
    }

    def unbind(key: String, value: LocalTime) = Map(key -> value.toString(pattern))
  }

  /**
   * Default formatter for `org.joda.time.LocalTime` type with pattern `HH:mm:ss`.
   *
   * @param pattern a date pattern as specified in `org.joda.time.format.DateTimeFormat`.
   */
  implicit val jodaLocalTimeFormat: Formatter[LocalTime] = jodaLocalTimeFormat("HH:mm:ss")
}
