package models.actor

import java.util.UUID

import akka.actor._
import controllers.ReservationRequest
import play.api.Logger

import scala.collection.mutable

class ReservationAggregateToModelMediator(reservationAggregateActor: ActorRef, forwardTo: ActorRef) extends Actor {

  override def receive: Actor.Receive = ???
}

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservation(req: ReservationRequest)

  case class GetReservation(reservationId: UUID)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {

  import ReservationsEngineActor._
  import ReservationAggregateActor._

  val logger: Logger = Logger(this.getClass)
  val reservationsEventLogRepositoryActor = context.actorOf(ReservationsEventLogRepositoryActor.props, "reservationsEventLogRepositoryActor")
  val reservationsCache = mutable.Map[ReservationId, ActorRef]()

  override def receive: Receive = {
    case MakeReservation(request) =>
      logger.info(s"Will try to make the reservation $request")
      val reservationId = UUID.randomUUID()
      val reservationAggregateActor = context.actorOf(ReservationAggregateActor.props(self, reservationsEventLogRepositoryActor, reservationId))
      reservationAggregateActor ! CreateReservation(request)
      reservationsCache(reservationId) = reservationAggregateActor
      sender ! reservationId

  }
}
