package tech.trodriguez.tennisbookings.back.actor

import java.util.UUID

import akka.actor._
import akka.pattern.pipe
import tech.trodriguez.tennisbookings.back.db.BookingsEventLogRepository
import play.api.Logger

import scala.language.implicitConversions

object BookingsEventLogRepositoryActor {
  val props = Props[BookingsEventLogRepositoryActor]

  case class SaveEvent(event: BookingAggregateActor.Event)

  case class EventSaved(event: BookingAggregateActor.Event)

  case class FindEvents(bookingId: UUID)

  case class EventsFound(events: Seq[BookingAggregateActor.Event])

}

/**
  * Repository for Booking
  */
class BookingsEventLogRepositoryActor extends Actor {

  import BookingsEventLogRepositoryActor._
  import context.dispatcher

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case SaveEvent(event) =>
      logger.debug(s"Will add event $event to event log")
      val eventualResponse = BookingsEventLogRepository.add(event).map(_ => EventSaved(event))
      eventualResponse pipeTo sender

    case FindEvents(bookingId) =>
      logger.debug(s"Will find the events of the booking $bookingId")
      val eventualEvents = BookingsEventLogRepository.findAllEvents(bookingId)
      val eventualMsg = eventualEvents.map(events => EventsFound(events))
      eventualMsg pipeTo sender
  }
}
