package models.actor

import java.util.NoSuchElementException

import akka.actor.{ActorRef, FSM, LoggingFSM, Props}
import models.db.{Reservation, ReservationRequest}
import play.api.Logger

object ReservationActor {

  // State
  sealed trait State

  case object Idle extends State

  case object PersistingIntoDb extends State

  case object LoadingFromDb extends State

  case object Persisted extends State

  // Data
  sealed trait Data

  case object Uninitialized extends Data

  case class PersistingIntoDb(req: ReservationRequest, notifyTo: ActorRef) extends Data

  case class LoadingFromDb(reservationId: Long, notifyTo: ActorRef) extends Data

  case class PersistedReservation(reservation: Reservation) extends Data

  // Operations
  case class Persist(req: ReservationRequest, notifyTo: ActorRef)

  case class PersistedIntoDb(reservation: Reservation, notifyTo: ActorRef)

  case class LoadFromDb(id: Long, notifyTo: ActorRef)

  case class LoadedFromDb(reservation: Reservation, notifyTo: ActorRef)

  case class NotFound(reservationId: Long, notifyTo: ActorRef)

  def props(reservationsEngineActor: ActorRef, reservationsRepositoryActor: ActorRef) =
    Props(classOf[ReservationActor], reservationsEngineActor, reservationsRepositoryActor)
}

class ReservationActor(reservationsEngineActor: ActorRef, reservationsRepositoryActor: ActorRef) extends LoggingFSM[ReservationActor.State, ReservationActor.Data] {

  import ReservationActor._
  import ReservationsRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(Persist(req, notifyTo), Uninitialized) =>
      reservationsRepositoryActor ! CreateReservationRequest(req)
      goto(PersistingIntoDb) using PersistingIntoDb(req, notifyTo)

    case Event(LoadFromDb(reservationId, notifyTo), Uninitialized) =>
      reservationsRepositoryActor ! FindReservationRequest(reservationId)
      goto(LoadingFromDb) using LoadingFromDb(reservationId, notifyTo)
  }

  when(PersistingIntoDb) {
    case Event(CreateReservationResponse(reservation), PersistingIntoDb(req, notifyTo)) =>
      reservationsEngineActor ! PersistedIntoDb(reservation, notifyTo)
      goto(Persisted) using PersistedReservation(reservation)
  }

  when(LoadingFromDb) {
    case Event(FindReservationResponse(maybeReservation), LoadingFromDb(reservationId, notifyTo)) =>
      maybeReservation match {
        case Some(reservation) =>
          reservationsEngineActor ! LoadedFromDb(reservation, notifyTo)
          goto(Persisted) using PersistedReservation(reservation)

        case None =>
          reservationsEngineActor ! NotFound(reservationId, notifyTo)
          stop(FSM.Failure(new NoSuchElementException(s"Couldn't find reservation with id $reservationId")))
      }
  }

  when(Persisted) {
    case Event(LoadFromDb(reservationId, notifyTo), PersistedReservation(reservation)) =>
      if (reservationId == reservation.id) {
        reservationsEngineActor ! LoadedFromDb(reservation, notifyTo)
        stay
      } else {
        reservationsRepositoryActor ! FindReservationRequest(reservationId)
        goto(LoadingFromDb) using LoadingFromDb(reservationId, notifyTo)
      }
  }

  initialize()
}
