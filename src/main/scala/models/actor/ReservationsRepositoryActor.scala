package models.actor

import akka.actor._
import akka.pattern.pipe
import models.db.{ReservationRequest, ReservationsRepository}
import play.api.Logger

import scala.language.implicitConversions

object ReservationsRepositoryActor {
  val props = Props[ReservationsRepositoryActor]

  case class CreateReservation(req: ReservationRequest)

}

/**
  * Repository for Booking
  */
class ReservationsRepositoryActor extends Actor {

  import ReservationsRepositoryActor._
  import context.dispatcher

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case CreateReservation(req) =>
      logger.debug(s"Will create a new reservation from request $req")
      pipe(ReservationsRepository.create(req)) to sender
  }
}
