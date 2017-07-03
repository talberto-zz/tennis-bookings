package tech.trodriguez.tennisbookings.back.controllers

import java.time.ZonedDateTime
import java.util.UUID

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import play.api._
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.mvc._
import tech.trodriguez.tennisbookings.back.Reservation
import tech.trodriguez.tennisbookings.back.actor.ReservationsEngineActor
import tech.trodriguez.tennisbookings.back.actor.ReservationsEngineActor.{FindReservation, MakeReservation}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object BookingRequest {
  implicit val JsonFormat = Json.format[BookingRequest]
}

case class BookingRequest(
                           dateTime: ZonedDateTime,
                           court: Int
                         )

class BookingsController @Inject()(configuration: Configuration, actorSystem: ActorSystem, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val askTimeout = Timeout(configuration.underlying.getDuration("akka.askTimeout").toNanos nanoseconds)

  val logger: Logger = Logger(this.getClass)

  lazy val reservationsEngine = actorSystem.actorOf(ReservationsEngineActor.props, "reservationsEngine")

  def create = Action.async(parse.json[BookingRequest]) { implicit request =>
    logger.debug("Received reservation creation request")

    val eventualResponse = reservationsEngine ? MakeReservation(request.body)
    val eventualReservationId = eventualResponse.mapTo[Reservation]

    render.async {
      case Accepts.Json() => eventualReservationId.map { reservation =>
        Created.withHeaders(HeaderNames.LOCATION -> routes.BookingsController.find(reservation.id).url)
      }
    }
  }

  def find(id: UUID) = Action.async { implicit req =>
    logger.debug(s"Try to find reservation $id")

    val eventualResponse = reservationsEngine ? FindReservation(id)
    val eventualMaybeReservation = eventualResponse.mapTo[Option[Reservation]]

    render.async {
      case Accepts.Json() => eventualMaybeReservation.map {
        case Some(reservation) => Ok(Json.toJson(reservation))
        case None => NotFound
      }
    }
  }
}
