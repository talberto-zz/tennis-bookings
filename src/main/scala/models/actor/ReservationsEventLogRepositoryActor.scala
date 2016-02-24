package models.actor

import akka.actor._
import models.db.ReservationsEventLogRepository
import play.api.Logger

import scala.language.implicitConversions

object ReservationsEventLogRepositoryActor {
  val props = Props[ReservationsEventLogRepositoryActor]

  case class SaveEvent(event: ReservationAggregateActor.Event)

}

/**
  * Repository for Booking
  */
class ReservationsEventLogRepositoryActor extends Actor {

  import ReservationsEventLogRepositoryActor._

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case SaveEvent(evt) =>
      logger.debug(s"Will add event $evt to event log")
      ReservationsEventLogRepository.add(evt)
  }
}
