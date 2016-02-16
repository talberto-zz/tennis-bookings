package models.actor

import java.util.UUID

import akka.actor._
import models.db.ReservationRequest
import play.api.Logger

import scala.collection.mutable

object ReservationsEngineActor {
  def props = Props[ReservationsEngineActor]

  case class MakeReservationRequest(req: ReservationRequest)

}

/**
  * Created by trodriguez on 14/02/16.
  */
class ReservationsEngineActor extends Actor {
  import models.actor.ReservationsEngineActor._
  import models.actor.ReservationsRepositoryActor._

  val logger: Logger = Logger(this.getClass)
  val reservationsRepositoryActor = context.actorOf(ReservationsRepositoryActor.props)
  val activeRequests = mutable.Map[UUID, ActorRef]()

  override def receive: Receive = {
    case MakeReservationRequest(req) =>
      logger.info(s"Will try to make the reservation $req")
      val creationReq = CreateReservationRequest(req = req)
      logger.debug(s"Issuing reservation creation request [$creationReq]")
      reservationsRepositoryActor ! creationReq
      activeRequests += creationReq.id -> sender

    case notification @ CreateReservationResponse(id, reservation) =>
      logger.debug(s"Received notification of reservation created [$notification]")
      val originalSender = activeRequests(id)
      originalSender ! reservation
      activeRequests -= id
  }
}
