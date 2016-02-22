package models.actor

import akka.actor._
import models.actor.ReservationsRepositoryActor.{CreateReservationResponse, FindReservationResponse}
import models.db.ReservationRequest
import play.api.Logger

object ReservationsRepositoryMediatorActor {
  def props(reservationsEngineActor: ActorRef, reservationsRepositoryActor: ActorRef, request: AnyRef, originalSender: ActorRef) =
    Props(classOf[ReservationsRepositoryMediatorActor], reservationsEngineActor, reservationsRepositoryActor, request, originalSender)
}

class ReservationsRepositoryMediatorActor(reservationsEngineActor: ActorRef, reservationsRepositoryActor: ActorRef, request: AnyRef, originalSender: ActorRef) extends Actor {
  val logger: Logger = Logger(this.getClass)

  reservationsRepositoryActor ! request

  override def receive: Receive = {
    case notification @ CreateReservationResponse(reservation) =>
      logger.debug(s"Received notification of reservation created [$notification]")
      originalSender tell (reservation, reservationsEngineActor)
      self ! PoisonPill

    case notification @ FindReservationResponse(maybeReservation) =>
      logger.debug(s"Received notification of find reservation [$notification]")
      originalSender tell (maybeReservation, reservationsEngineActor)
      self ! PoisonPill

  }
}

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservation(req: ReservationRequest)

  case class GetReservation(reservationId: Long)
}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {
  import models.actor.ReservationsEngineActor._
  import models.actor.ReservationsRepositoryActor._

  val logger: Logger = Logger(this.getClass)
  val reservationsRepositoryActor = context.actorOf(ReservationsRepositoryActor.props, "reservationsRepository")

  override def receive: Receive = {
    case MakeReservation(req) =>
      logger.info(s"Will try to make the reservation $req")
      val creationReq = CreateReservationRequest(req)
      logger.debug(s"Issuing reservation creation request [$creationReq]")
      context.actorOf(ReservationsRepositoryMediatorActor.props(self, reservationsRepositoryActor, creationReq, sender))

    case msg @ GetReservation(id) =>
      logger.debug(s"Received get reservation message $msg")
      val findReq = FindReservationRequest(id)
      context.actorOf(ReservationsRepositoryMediatorActor.props(self, reservationsRepositoryActor, findReq, sender))
  }
}
