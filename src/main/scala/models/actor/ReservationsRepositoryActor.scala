package models.actor

import akka.actor._
import akka.pattern.pipe
import models.db.{Reservation, ReservationRequest, ReservationsRepository}
import play.api.Logger

import scala.language.implicitConversions

object ReservationsRepositoryActor {
  val props = Props[ReservationsRepositoryActor]

  case class CreateReservationRequest(req: ReservationRequest)

  case class CreateReservationResponse(reservation: Reservation) {}

  case class FindReservationRequest(reservationId: Long)

  case class FindReservationResponse(reservation: Option[Reservation])

}

/**
  * Repository for Booking
  */
class ReservationsRepositoryActor extends Actor {

  import ReservationsRepositoryActor._
  import context.dispatcher

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case CreateReservationRequest(req) =>
      logger.debug(s"Will create a new reservation from request $req")
      val resp = ReservationsRepository.create(req).map(reservation => CreateReservationResponse(reservation))
      pipe(resp) to sender

    case FindReservationRequest(reservationId) =>
      logger.debug(s"Will try to find the reservation $reservationId")
      val resp = ReservationsRepository.find(reservationId).map(maybeReservation => FindReservationResponse(maybeReservation))
      pipe(resp) to sender
  }
}
