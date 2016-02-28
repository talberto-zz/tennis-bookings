package models.actor

import java.time.ZonedDateTime
import java.util.UUID

import akka.actor.{LoggingFSM, Props}
import controllers.ReservationRequest
import models.Reservation
import play.api.Logger
import play.api.libs.json._

object ReservationAggregateActor {

  type ReservationId = UUID

  trait IdentifiedReservation {
    def reservationId: ReservationId
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

  case class CreateReservation(req: ReservationRequest) extends Command

  // Events
  object Event {

    implicit object JsonWrites extends Writes[Event] {
      def writes(event: Event) = event match {

        case e: ReservationCreated =>
          Json.toJson(e)(ReservationCreated.JsonWrites)

        case e =>
          throw new RuntimeException(s"Unknown event $e")
      }
    }

    implicit object JsonReads extends Reads[Event] {
      override def reads(json: JsValue): JsResult[Event] = {
        val className = (json \ "@class").get.as[String]

        if (className == ReservationCreated.getClass.getName)
          Json.fromJson[ReservationCreated](json)(ReservationCreated.JsonReads)
        else
          throw new RuntimeException(s"Unknown class $className")

      }
    }

  }

  sealed trait Event extends IdentifiedReservation

  object ReservationCreated {

    implicit val JsonWrites: Writes[ReservationCreated] = new Writes[ReservationCreated] {
      override def writes(event: ReservationCreated): JsValue = Json.obj(
        "@class" -> ReservationCreated.getClass.getName,
        "reservationId" -> event.reservationId,
        "creationDate" -> event.creationDate,
        "lastModified" -> event.lastModified,
        "dateTime" -> event.dateTime,
        "court" -> event.court
      )
    }

    implicit val JsonReads = Json.reads[ReservationCreated]

  }

  case class ReservationCreated(
                                 reservationId: ReservationId,
                                 creationDate: ZonedDateTime,
                                 lastModified: ZonedDateTime,
                                 dateTime: ZonedDateTime,
                                 court: Int
                               ) extends Event

  // Other messages
  case class GetReservationView()

  case class ReservationViewComputed(reservation: Reservation)

  def props(reservationId: ReservationAggregateActor.ReservationId) =
    Props(classOf[ReservationAggregateActor], reservationId)
}

class ReservationAggregateActor(
                                 reservationId: ReservationAggregateActor.ReservationId
                               ) extends LoggingFSM[ReservationAggregateActor.State, ReservationAggregateActor.Data] {

  import ReservationAggregateActor._

  val logger: Logger = Logger(this.getClass)

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(CreateReservation(req), Uninitialized) =>
      logger.debug(s"Received creation request $req")
      val event = ReservationCreated(
        reservationId = reservationId,
        creationDate = ZonedDateTime.now(),
        lastModified = ZonedDateTime.now(),
        dateTime = req.dateTime,
        court = req.court
      )
      sender ! event
      goto(New) using Created(
        creationDate = event.creationDate,
        lastModified = event.lastModified,
        dateTime = event.dateTime,
        court = event.court
      )

    case Event(event: ReservationCreated, Uninitialized) =>
      logger.debug(s"Replaying event $event")
      goto(New) using Created(
        creationDate = event.creationDate,
        lastModified = event.lastModified,
        dateTime = event.dateTime,
        court = event.court
      )
  }

  when(New) {
    case Event(GetReservationView(), created: Created) =>
      logger.debug(s"Will compute and return the reservation view to $sender")
      val view = Reservation(
        id = reservationId,
        dateTime = created.dateTime,
        court = created.court
      )
      sender ! ReservationViewComputed(view)
      stay()
  }
}
