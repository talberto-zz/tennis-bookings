package tech.trodriguez.tennisbookings.back.actor

import java.util.UUID

import akka.actor._
import play.api.Logger
import tech.trodriguez.tennisbookings.back.controllers.BookingRequest

class MakeReservationMediator(reservationsEngineActor: ActorRef, reservationsEventLogRepositoryActor: ActorRef, request: BookingRequest, notifyTo: ActorRef) extends Actor {

  import BookingAggregateActor._
  import BookingsEventLogRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  val reservationId = UUID.randomUUID()
  logger.info(s"Creating new reservation $reservationId")
  val reservationAggregateActor = context.actorOf(BookingAggregateActor.props(reservationId), s"reservation-$reservationId")

  logger.debug(s"Asking for all events for reservation $reservationAggregateActor with id $reservationId")
  reservationsEventLogRepositoryActor ! FindEvents(reservationId)

  override def receive: Actor.Receive = {
    case EventsFound(events) =>
      val createReservation = CreateReservation(request)
      logger.debug(s"Received #${events.size} events for reservation $reservationAggregateActor with $reservationId. Will replay them and send the command $createReservation")
      events.foreach { event =>
        logger.debug(s"Will send event $event to reservation $reservationAggregateActor with id $reservationId")
        reservationAggregateActor ! event
      }
      reservationAggregateActor ! createReservation

    case reservationCreated: ReservationCreated =>
      logger.debug(s"Reservation successfully created $reservationCreated. Will ask to persist the event")
      reservationsEventLogRepositoryActor ! SaveEvent(reservationCreated)

    case event: EventSaved =>
      logger.debug(s"Event correctly persisted. Will ask a reservation view and notify the original requester $notifyTo")
      reservationAggregateActor ! GetReservationView()

    case ReservationViewComputed(reservation) =>
      logger.debug(s"Reservation view received $reservation. Will notify the original requester $notifyTo")
      notifyTo ! reservation
      logger.debug(s"Will finish the reservation aggregate $reservationAggregateActor with id $reservationId")
      reservationAggregateActor ! PoisonPill
      self ! PoisonPill
  }
}

class FindReservationMediator(reservationsEngineActor: ActorRef, reservationsEventLogRepositoryActor: ActorRef, reservationId: UUID, notifyTo: ActorRef) extends Actor {

  import BookingAggregateActor._
  import BookingsEventLogRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  logger.info(s"Creating new reservation $reservationId")
  val reservationAggregateActor = context.actorOf(BookingAggregateActor.props(reservationId), s"reservation-$reservationId")

  logger.debug(s"Asking for all events for reservation $reservationAggregateActor with id $reservationId")
  reservationsEventLogRepositoryActor ! FindEvents(reservationId)

  override def receive: Receive = {
    case EventsFound(events) =>
      events.size match {
        case 0 =>
          logger.debug(s"Received 0 events for reservation $reservationAggregateActor with id $reservationId. Will notify original requester that reservation does not exist")
          notifyTo ! None
          reservationAggregateActor ! PoisonPill
          self ! PoisonPill
          
        case n =>
          logger.debug(s"Received #$n events for reservation $reservationAggregateActor with id $reservationId. Will replay them and ask a reservation view in order to notify the original requester $notifyTo")
          events.foreach { event =>
            logger.debug(s"Will send event $event to reservation $reservationAggregateActor with id $reservationId")
            reservationAggregateActor ! event
          }
          logger.debug(s"Asking reservation view to reservation $reservationAggregateActor with id $reservationId")
          reservationAggregateActor ! GetReservationView()
      }

    case ReservationViewComputed(reservation) =>
      logger.debug(s"Reservation view received $reservation. Will notify the original requester $notifyTo")
      notifyTo ! Some(reservation)
      logger.debug(s"Will finish the reservation aggregate $reservationAggregateActor with id $reservationId")
      reservationAggregateActor ! PoisonPill
      self ! PoisonPill
  }
}

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservation(req: BookingRequest)

  case class FindReservation(reservationId: UUID)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {

  import ReservationsEngineActor._

  val logger: Logger = Logger(this.getClass)
  val reservationsEventLogRepositoryActor = context.actorOf(BookingsEventLogRepositoryActor.props, "reservationsEventLogRepositoryActor")

  override def receive: Receive = {
    case MakeReservation(request) =>
      context.actorOf(Props(classOf[MakeReservationMediator], self, reservationsEventLogRepositoryActor, request, sender))

    case FindReservation(reservationId) =>
      context.actorOf(Props(classOf[FindReservationMediator], self, reservationsEventLogRepositoryActor, reservationId, sender))

  }
}
