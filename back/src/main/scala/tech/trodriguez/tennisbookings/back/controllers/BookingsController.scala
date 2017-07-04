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
import tech.trodriguez.tennisbookings.back.Booking
import tech.trodriguez.tennisbookings.back.actor.BookingsEngineActor
import tech.trodriguez.tennisbookings.back.actor.BookingsEngineActor.{FindBooking, MakeBooking}

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

  lazy val bookingsEngine = actorSystem.actorOf(BookingsEngineActor.props, "bookingsEngine")

  def create = Action.async(parse.json[BookingRequest]) { implicit request =>
    logger.debug("Received booking creation request")

    val eventualResponse = bookingsEngine ? MakeBooking(request.body)
    val eventualBookingId = eventualResponse.mapTo[Booking]

    render.async {
      case Accepts.Json() => eventualBookingId.map { booking =>
        Created.withHeaders(HeaderNames.LOCATION -> routes.BookingsController.find(booking.id).url)
      }
    }
  }

  def find(id: UUID) = Action.async { implicit req =>
    logger.debug(s"Try to find booking $id")

    val eventualResponse = bookingsEngine ? FindBooking(id)
    val eventualMaybeBooking = eventualResponse.mapTo[Option[Booking]]

    render.async {
      case Accepts.Json() => eventualMaybeBooking.map {
        case Some(booking) => Ok(Json.toJson(booking))
        case None => NotFound
      }
    }
  }
}
