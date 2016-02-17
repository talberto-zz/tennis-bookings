package models.actor

import java.util.UUID

import akka.actor._
import akka.pattern.pipe
import models.db.{Reservation, ReservationRequest, ReservationsRepository}
import play.api.Logger

import scala.language.implicitConversions

object ReservationsRepositoryActor {
  val props = Props[ReservationsRepositoryActor]

  case class CreateReservationRequest(id: UUID = UUID.randomUUID(), req: ReservationRequest)

  case class CreateReservationResponse(id: UUID, reservation: Reservation) {}

  case class FindReservationRequest(id: UUID = UUID.randomUUID(), reservationId: Long)

  case class FindReservationResponse(id: UUID = UUID.randomUUID(), reservation: Option[Reservation])

}

/**
  * Repository for Booking
  */
class ReservationsRepositoryActor extends Actor {

  import ReservationsRepositoryActor._
  import context.dispatcher

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case CreateReservationRequest(id, req) =>
      logger.debug(s"Will create a new reservation from request $req")
      val resp = ReservationsRepository.create(req).map(reservation => CreateReservationResponse(id, reservation))
      pipe(resp) to sender

    case FindReservationRequest(id, reservationId) =>
      logger.debug(s"Will try to find the reservation $reservationId")
      val resp = ReservationsRepository.find(reservationId).map(maybeReservation => FindReservationResponse(id, maybeReservation))
      pipe(resp) to sender
  }
}
