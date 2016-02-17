package controllers

import akka.pattern.ask
import akka.util.Timeout
import models.actor.ReservationsEngineActor
import models.actor.ReservationsEngineActor.{GetReservation, MakeReservation}
import models.db.{Reservation, ReservationRequest}
import play.api.Play.{configuration, current}
import play.api._
import play.api.http.HeaderNames
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration._

object ReservationsControllerConfiguration {
  implicit val askTimeout = Timeout(configuration.underlying.getDuration("akka.askTimeout").toNanos nanoseconds)
}

object ReservationsController extends Controller {

  import ReservationsControllerConfiguration._
  
  val actorSystem = Akka.system
  val logger: Logger = Logger(this.getClass)

  lazy val reservationsEngine = actorSystem.actorOf(ReservationsEngineActor.props)

  def create = Action.async(parse.json[ReservationRequest]) { implicit request =>
    logger.debug("Received reservation creation request")

    val eventualResponse = reservationsEngine ? MakeReservation(request.body)
    val eventualReservation = eventualResponse.mapTo[Reservation]

    render.async {
      case Accepts.Json() => eventualReservation.map { reservation =>
        Created.withHeaders(HeaderNames.LOCATION -> routes.ReservationsController.find(reservation.id).url)
      }
    }
  }

  def find(id: Long) = Action.async { implicit request =>
    logger.debug(s"Try to find reservation $id")

    val eventualResponse = reservationsEngine ? GetReservation(id)
    val eventualMaybeReservation = eventualResponse.mapTo[Option[Reservation]]

    render.async {
      case Accepts.Json() => eventualMaybeReservation.map {
        case Some(reservation) => Ok(Json.toJson(reservation))
        case None => NotFound
      }
    }
  }
}
