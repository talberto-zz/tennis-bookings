package models

import scala.language.implicitConversions // remove implicit conversion warnings
import models.Booking.Implicits._
import play.api.mvc.QueryStringBindable

/**
 * 
 */
object Binders {
  implicit def BookingBinder(implicit intBinder: QueryStringBindable[Int], longBinder: QueryStringBindable[Long]) = new QueryStringBindable[Booking] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Booking]] = {
      for {
        id <- longBinder.bind(key + ".id", params)
        date <- longBinder.bind(key + ".date", params)
        status <- intBinder.bind(key + ".status", params)
      } yield {
        (id, date, status) match {
          case (Right(id), Right(date), Right(status)) => Right(Booking(Some(id), date, status))
          case _ => Left("Unable to bind a Booking")
        }
      }
    }
    
    override def unbind(key: String, booking: Booking): String = {
      longBinder.unbind(key + ".id", booking.id.get) + "&" + longBinder.unbind(key + ".date", booking.date.getMillis) + "&" + intBinder.unbind(key + ".status", booking.status.id)
    }
  }
}