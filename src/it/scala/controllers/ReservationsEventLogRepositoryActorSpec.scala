package controllers

import java.time.ZonedDateTime
import java.util.UUID

import akka.testkit.{TestKit, TestProbe}
import models.actor.ReservationAggregateActor.ReservationCreated
import models.actor.ReservationsEventLogRepositoryActor
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import play.api.libs.concurrent.Akka

import scala.concurrent.duration._

class ReservationsEventLogRepositoryActorSpec extends WordSpec
  with BeforeAndAfterAll
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen
  with WithConfiguredServerPerTest
  with ConfigurableScaleFactor {

  implicit lazy val system = Akka.system

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "ReservationsEventLogRepositoryActor" must {
    import ReservationsEventLogRepositoryActor._

    "save an event" in {
      val probe = TestProbe()
      implicit val sender = probe.ref

      Given("A reservation event")
      val reservationsEventLogRepositoryActor = system.actorOf(ReservationsEventLogRepositoryActor.props, "reservationsEventLogRepositoryActor")
      val reservationId = UUID.randomUUID()
      val event = ReservationCreated(
        reservationId = reservationId,
        creationDate = ZonedDateTime.now(),
        lastModified = ZonedDateTime.now(),
        dateTime = ZonedDateTime.now(),
        court = 1
      )

      When("We save it")
      probe.within(5 seconds) {
        reservationsEventLogRepositoryActor ! SaveEvent(event)
        probe.expectMsg(EventSaved(event))
      }

      Then("We are able to get it later")
      probe.within(5 seconds) {
        reservationsEventLogRepositoryActor ! FindEvents(reservationId)
        probe.expectMsg(EventsFound(Seq(event)))
      }
    }
  }
}
