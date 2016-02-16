package controllers

import akka.pattern.ask
import akka.util.Timeout
import models.actor.ReservationsEngineActor
import models.actor.ReservationsEngineActor.MakeReservationRequest
import models.db.{Reservation, ReservationRequest}
import play.api.Play.current
import play.api._
import play.api.http.HeaderNames
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration._

object ReservationsController extends Controller {

  implicit val askTimeout = Timeout(5 seconds)
  val actorSystem = Akka.system
  val logger: Logger = Logger(this.getClass)

  lazy val reservationsEngine = actorSystem.actorOf(ReservationsEngineActor.props)

  def create = Action.async(parse.json[ReservationRequest]) { implicit request =>
    logger.debug("Received reservation creation request")

    val eventualResponse = reservationsEngine ? MakeReservationRequest(request.body)
    val eventualReservation = eventualResponse.mapTo[Reservation]

    render.async {
      case Accepts.Json() => eventualReservation.map { reservation =>
        Created.withHeaders(HeaderNames.LOCATION -> routes.ReservationsController.find(reservation.id).url)
      }
    }
  }

  def find(id: Long) = Action {
    logger.debug(s"Try to find reservation $id")
    NotFound
  }
}
