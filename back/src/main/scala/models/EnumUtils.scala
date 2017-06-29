package models

import play.api.libs.json._

/**
  * Created by trodriguez on 14/02/16.
  */
object EnumUtils {
  implicit def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = (json: JsValue) => json match {
    case JsString(s) =>
      try {
        JsSuccess(enum.withName(s))
      } catch {
        case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
      }
    case _ => JsError("String value expected")
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = (v: E#Value) => JsString(v.toString)

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(EnumUtils.enumReads(enum), EnumUtils.enumWrites)
  }
}
