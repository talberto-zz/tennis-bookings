package tech.trodriguez.tennisbookings.back.actor

import java.time.ZonedDateTime
import java.util.UUID

import akka.actor.{LoggingFSM, Props}
import play.api.Logger
import play.api.libs.json._
import tech.trodriguez.tennisbookings.back.Booking
import tech.trodriguez.tennisbookings.back.controllers.BookingRequest

object BookingAggregateActor {

  type BookingId = UUID

  trait IdentifiedBooking {
    def bookingId: BookingId
  }

  // State
  sealed trait State

  case object Idle extends State

  case object New extends State

  // Data
  sealed trait Data

  case object Uninitialized extends Data

  case class Created(
                      creationDate: ZonedDateTime,
                      lastModified: ZonedDateTime,
                      dateTime: ZonedDateTime,
                      court: Int
                    ) extends Data

  // Commands
  sealed trait Command

  case class CreateBooking(req: BookingRequest) extends Command

  // Events
  object Event {

    implicit object JsonWrites extends Writes[Event] {
      def writes(event: Event) = event match {

        case e: BookingCreated =>
          Json.toJson(e)(BookingCreated.JsonWrites)

        case e =>
          throw new RuntimeException(s"Unknown event $e")
      }
    }

    implicit object JsonReads extends Reads[Event] {
      override def reads(json: JsValue): JsResult[Event] = {
        val className = (json \ "@class").get.as[String]

        if (className == BookingCreated.getClass.getName)
          Json.fromJson[BookingCreated](json)(BookingCreated.JsonReads)
        else
          throw new RuntimeException(s"Unknown class $className")

      }
    }

  }

  sealed trait Event extends IdentifiedBooking {
    def eventDateTime: ZonedDateTime
  }

  object BookingCreated {

    implicit val JsonWrites: Writes[BookingCreated] = new Writes[BookingCreated] {
      override def writes(event: BookingCreated): JsValue = Json.obj(
        "@class" -> BookingCreated.getClass.getName,
        "eventDateTime" -> event.eventDateTime,
        "bookingId" -> event.bookingId,
        "dateTime" -> event.dateTime,
        "court" -> event.court
      )
    }

    implicit val JsonReads = Json.reads[BookingCreated]

  }

  case class BookingCreated(
                                 bookingId: BookingId,
                                 eventDateTime: ZonedDateTime,
                                 dateTime: ZonedDateTime,
                                 court: Int
                               ) extends Event

  // Other messages
  case class GetBookingView()

  case class BookingViewComputed(booking: Booking)

  def props(bookingId: BookingAggregateActor.BookingId) =
    Props(classOf[BookingAggregateActor], bookingId)
}

class BookingAggregateActor(
                                 bookingId: BookingAggregateActor.BookingId
                               ) extends LoggingFSM[BookingAggregateActor.State, BookingAggregateActor.Data] {

  import BookingAggregateActor._

  val logger: Logger = Logger(this.getClass)

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(CreateBooking(req), Uninitialized) =>
      logger.debug(s"Received creation request $req")
      val event = BookingCreated(
        eventDateTime = ZonedDateTime.now(),
        bookingId = bookingId,
        dateTime = req.dateTime,
        court = req.court
      )
      sender ! event
      goto(New) using Created(
        creationDate = event.eventDateTime,
        lastModified = event.eventDateTime,
        dateTime = event.dateTime,
        court = event.court
      )

    case Event(event: BookingCreated, Uninitialized) =>
      logger.debug(s"Replaying event $event")
      goto(New) using Created(
        creationDate = event.eventDateTime,
        lastModified = event.eventDateTime,
        dateTime = event.dateTime,
        court = event.court
      )
  }

  when(New) {
    case Event(GetBookingView(), created: Created) =>
      logger.debug(s"Will compute and return the booking view to $sender")
      val view = Booking(
        id = bookingId,
        dateTime = created.dateTime,
        court = created.court
      )
      sender ! BookingViewComputed(view)
      stay()
  }
}
