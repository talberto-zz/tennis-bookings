package tech.trodriguez.tennisbookings.back.db

import java.time.ZonedDateTime
import java.util.UUID

import tech.trodriguez.tennisbookings.back.actor.BookingAggregateActor
import play.api.Logger
import play.api.libs.json.Json
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

private[db] case class BookingEvent(
                                         id: Long,
                                         eventDateTime: ZonedDateTime,
                                         bookingId: UUID,
                                         event: BookingAggregateActor.Event
                                       )

private[db] class BookingsEvents(tag: Tag) extends Table[BookingEvent](tag, "bookings_events") {

  object Converters {
    implicit val jsonColumnType = MappedColumnType.base[BookingAggregateActor.Event, String](
      { event => Json.toJson(event).toString }, { str => Json.parse(str).as[BookingAggregateActor.Event] }
    )
  }

  import SlickConverters._
  import Converters._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def eventDateTime = column[ZonedDateTime]("event_date_time")

  def bookingId = column[UUID]("booking_id")

  def event = column[BookingAggregateActor.Event]("event")

  def * = (id, eventDateTime, bookingId, event) <>((BookingEvent.apply _).tupled, BookingEvent.unapply)
}

object BookingsEventLogRepository {

  val logger: Logger = Logger(this.getClass)
  val db = Sandbox.db

  private val bookingsEvent = TableQuery[BookingsEvents]

  def add(event: BookingAggregateActor.Event)(implicit executionContext: ExecutionContext): Future[Unit] = {
    logger.debug(s"Will add event $event to event log")
    val action = (bookingsEvent returning bookingsEvent.map(_.id) into ((b, id) => b.copy(id = id))) += BookingEvent(0, event.eventDateTime, event.bookingId, event)
    db.run(action).map(_ => ())
  }

  def findAllEvents(bookingId: UUID)(implicit executionContext: ExecutionContext): Future[Seq[BookingAggregateActor.Event]] = {
    logger.debug(s"Will find the events of the booking $bookingId")
    val action = bookingsEvent.filter(_.bookingId === bookingId).result
    db.run(action).map { seq =>
      logger.debug(s"Found #${seq.size} events for booking $bookingId")
      seq.map(evt => evt.event)
    }
  }
}
