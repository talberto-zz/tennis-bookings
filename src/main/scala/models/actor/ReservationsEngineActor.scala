package models.actor

import akka.actor._
import models.db.ReservationRequest
import play.api.Logger

import scala.collection.mutable

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservation(req: ReservationRequest)

  case class GetReservation(reservationId: Long)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {

  import ReservationActor._
  import ReservationsEngineActor._

  val logger: Logger = Logger(this.getClass)
  val reservationsRepositoryActor = context.actorOf(ReservationsRepositoryActor.props, "reservationsRepository")
  val reservationsCache = mutable.Map[Long, ActorRef]()

  override def receive: Receive = {
    case MakeReservation(request) =>
      logger.info(s"Will try to make the reservation $request")
      val reservationActor = context.actorOf(ReservationActor.props(self, reservationsRepositoryActor))
      reservationActor ! Persist(request, sender)

    case GetReservation(reservationId) =>
      logger.info(s"Got request for getting reservation $reservationId")
      val reservationActor = reservationsCache.getOrElse(reservationId, context.actorOf(ReservationActor.props(self, reservationsRepositoryActor)))
      reservationActor ! LoadFromDb(reservationId, sender)

    case PersistedIntoDb(reservation, notifyTo) =>
      logger.info(s"Got notified of persisted reservation $reservation")
      reservationsCache += reservation.id -> sender
      notifyTo ! reservation

    case LoadedFromDb(reservation, notifyTo) =>
      logger.info(s"Got notified of reservation loaded $reservation")
      reservationsCache += reservation.id -> sender
      notifyTo ! Some(reservation)

    case NotFound(reservationId, notifyTo) =>
      logger.info(s"Got notified of a reservation not found $reservationId")
      reservationsCache += reservationId -> sender
      notifyTo ! None
  }
}
