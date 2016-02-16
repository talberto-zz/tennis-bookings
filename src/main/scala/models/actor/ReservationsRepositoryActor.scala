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
  }
}
