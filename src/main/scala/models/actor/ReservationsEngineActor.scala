package models.actor

import akka.actor._
import models.db.ReservationRequest
import play.api.Logger

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservationReq(req: ReservationRequest)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {
  import models.actor.ReservationsEngineActor._

  val logger: Logger = Logger(this.getClass)

  override def receive: Receive = {
    case MakeReservationReq(req) =>
      logger.info(s"Will try to make the reservation $req")

  }
}
