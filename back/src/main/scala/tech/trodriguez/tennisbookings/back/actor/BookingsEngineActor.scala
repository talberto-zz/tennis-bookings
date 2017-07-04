package tech.trodriguez.tennisbookings.back.actor

import java.util.UUID

import akka.actor._
import play.api.Logger
import tech.trodriguez.tennisbookings.back.controllers.BookingRequest

class MakeBookingMediator(bookingsEngineActor: ActorRef, bookingsEventLogRepositoryActor: ActorRef, request: BookingRequest, notifyTo: ActorRef) extends Actor {

  import BookingAggregateActor._
  import BookingsEventLogRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  val bookingId = UUID.randomUUID()
  logger.info(s"Creating new booking $bookingId")
  val bookingAggregateActor = context.actorOf(BookingAggregateActor.props(bookingId), s"booking-$bookingId")

  logger.debug(s"Asking for all events for booking $bookingAggregateActor with id $bookingId")
  bookingsEventLogRepositoryActor ! FindEvents(bookingId)

  override def receive: Actor.Receive = {
    case EventsFound(events) =>
      val createBooking = CreateBooking(request)
      logger.debug(s"Received #${events.size} events for booking $bookingAggregateActor with $bookingId. Will replay them and send the command $createBooking")
      events.foreach { event =>
        logger.debug(s"Will send event $event to booking $bookingAggregateActor with id $bookingId")
        bookingAggregateActor ! event
      }
      bookingAggregateActor ! createBooking

    case bookingCreated: BookingCreated =>
      logger.debug(s"Booking successfully created $bookingCreated. Will ask to persist the event")
      bookingsEventLogRepositoryActor ! SaveEvent(bookingCreated)

    case event: EventSaved =>
      logger.debug(s"Event correctly persisted. Will ask a booking view and notify the original requester $notifyTo")
      bookingAggregateActor ! GetBookingView()

    case BookingViewComputed(booking) =>
      logger.debug(s"Booking view received $booking. Will notify the original requester $notifyTo")
      notifyTo ! booking
      logger.debug(s"Will finish the booking aggregate $bookingAggregateActor with id $bookingId")
      bookingAggregateActor ! PoisonPill
      self ! PoisonPill
  }
}

class FindBookingMediator(bookingsEngineActor: ActorRef, bookingsEventLogRepositoryActor: ActorRef, bookingId: UUID, notifyTo: ActorRef) extends Actor {

  import BookingAggregateActor._
  import BookingsEventLogRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  logger.info(s"Creating new booking $bookingId")
  val bookingAggregateActor = context.actorOf(BookingAggregateActor.props(bookingId), s"booking-$bookingId")

  logger.debug(s"Asking for all events for booking $bookingAggregateActor with id $bookingId")
  bookingsEventLogRepositoryActor ! FindEvents(bookingId)

  override def receive: Receive = {
    case EventsFound(events) =>
      events.size match {
        case 0 =>
          logger.debug(s"Received 0 events for booking $bookingAggregateActor with id $bookingId. Will notify original requester that booking does not exist")
          notifyTo ! None
          bookingAggregateActor ! PoisonPill
          self ! PoisonPill
          
        case n =>
          logger.debug(s"Received #$n events for booking $bookingAggregateActor with id $bookingId. Will replay them and ask a booking view in order to notify the original requester $notifyTo")
          events.foreach { event =>
            logger.debug(s"Will send event $event to booking $bookingAggregateActor with id $bookingId")
            bookingAggregateActor ! event
          }
          logger.debug(s"Asking booking view to booking $bookingAggregateActor with id $bookingId")
          bookingAggregateActor ! GetBookingView()
      }

    case BookingViewComputed(booking) =>
      logger.debug(s"Booking view received $booking. Will notify the original requester $notifyTo")
      notifyTo ! Some(booking)
      logger.debug(s"Will finish the booking aggregate $bookingAggregateActor with id $bookingId")
      bookingAggregateActor ! PoisonPill
      self ! PoisonPill
  }
}

object BookingsEngineActor {
  def props = Props[BookingsEngineActor]

  case class MakeBooking(req: BookingRequest)

  case class FindBooking(bookingId: UUID)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class BookingsEngineActor extends Actor {

  import BookingsEngineActor._

  val logger: Logger = Logger(this.getClass)
  val bookingsEventLogRepositoryActor = context.actorOf(BookingsEventLogRepositoryActor.props, "bookingsEventLogRepositoryActor")

  override def receive: Receive = {
    case MakeBooking(request) =>
      context.actorOf(Props(classOf[MakeBookingMediator], self, bookingsEventLogRepositoryActor, request, sender))

    case FindBooking(bookingId) =>
      context.actorOf(Props(classOf[FindBookingMediator], self, bookingsEventLogRepositoryActor, bookingId, sender))

  }
}
