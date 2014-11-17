package models

import scala.language.implicitConversions // remove implicit conversion warnings
import models.db.ReservationRequest
import models.db.Implicits._
import play.api.mvc.QueryStringBindable

object Binders {
  implicit def reservationRequestBinder(implicit intBinder: QueryStringBindable[Int], longBinder: QueryStringBindable[Long]) = new QueryStringBindable[ReservationRequest] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ReservationRequest]] = {
      for {
        id <- intBinder.bind(key + ".id", params)
        date <- longBinder.bind(key + ".date", params)
        status <- intBinder.bind(key + ".status", params)
      } yield {
        (id, date, status) match {
          case (Right(id), Right(date), Right(status)) => Right(ReservationRequest(Some(id), date, status))
          case _ => Left("Unable to bind a ReservationRequest")
        }
      }
    }
    
    override def unbind(key: String, request: ReservationRequest): String = {
      intBinder.unbind(key + ".id", request.id.get) + "&" + longBinder.unbind(key + ".date", request.date.getMillis) + "&" + intBinder.unbind(key + ".status", request.status.id)
    }
  }
}