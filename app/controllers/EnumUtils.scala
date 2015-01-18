package controllers

import scala.language.implicitConversions // remove implicit conversion warnings
import play.api.libs.json._

/**
 * Utility object to use enumerations with Form's
 */
object EnumUtils {
  /**
   * Defines a Reads for an enumeration
   */
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsNumber(n) => {
        try {
          JsSuccess(enum(n.toInt))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the id: '$n'")
        }
      }
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }
  
  /**
   * Defines a Writes for an enumeration
   */
  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }
  
  /**
   * Defines a Format for an enumeration
   */
  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumReads(enum), enumWrites)
  }
}
