package models.db

import java.util.UUID

import models.actor.ReservationAggregateActor
import play.api.Logger
import play.api.libs.json.Json
import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

case class ReservationEvent(id: Long, reservationId: UUID, event: ReservationAggregateActor.Event)

class ReservationsEvents(tag: Tag) extends Table[ReservationEvent](tag, "reservations_events") {

  object Converters {
    implicit val jsonColumnType = MappedColumnType.base[ReservationAggregateActor.Event, String](
      { event => Json.toJson(event).toString },
      { str => Json.parse(str).as[ReservationAggregateActor.Event] }
    )
  }

  import Converters._

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def reservationId = column[UUID]("reservation_id")

  def event = column[ReservationAggregateActor.Event]("event")

  def * = (id, reservationId, event) <>((ReservationEvent.apply _).tupled, ReservationEvent.unapply)
}

/**
  * Created by trodriguez on 16/02/16.
  */
/**
  * Repository for Booking
  */
object ReservationsEventLogRepository {

  val logger: Logger = Logger(this.getClass)
  val db = Sandbox.db

  private val reservationsEvent = TableQuery[ReservationsEvents]

  def add(event: ReservationAggregateActor.Event)(implicit executionContext: ExecutionContext): Future[Unit] = {
    logger.debug(s"Will add event $event to event log")
    val action = (reservationsEvent returning reservationsEvent.map(_.id) into ((b, id) => b.copy(id = id))) += ReservationEvent(0, event.reservationId, event)
    db.run(action).map(_ => ())
  }

  def findAllEvents(reservationId: UUID)(implicit executionContext: ExecutionContext): Future[Seq[ReservationAggregateActor.Event]] = {
    logger.debug(s"Will find the events of the reservation $reservationId")
    val action = reservationsEvent.filter(_.reservationId === reservationId).result
    db.run(action).map { seq =>
      logger.debug(s"Found #${seq.size} events for reservation $reservationId")
      seq.map(evt => evt.event)
    }
  }
}
