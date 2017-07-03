package tech.trodriguez.tennisbookings.back.actor

import java.time.ZonedDateTime
import java.util.UUID

import akka.testkit.{TestKit, TestProbe}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import play.api.inject.guice.GuiceApplicationBuilder
import tech.trodriguez.tennisbookings.back.actor.BookingAggregateActor.ReservationCreated
import tech.trodriguez.tennisbookings.back.docker.WithConfiguredServerPerTest
import tech.trodriguez.tennisbookings.back.util.ConfigurableScaleFactor

import scala.concurrent.duration._

class BookingsEventLogRepositoryActorSpec extends WordSpec
  with BeforeAndAfterAll
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen
  with WithConfiguredServerPerTest
  with ConfigurableScaleFactor {

  lazy val application = new GuiceApplicationBuilder().build()
  lazy val injector = application.injector
  implicit lazy val system = application.actorSystem

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "ReservationsEventLogRepositoryActor" must {
    import BookingsEventLogRepositoryActor._

    "save an event" in {
      val probe = TestProbe()
      implicit val sender = probe.ref

      Given("A reservation event")
      val reservationsEventLogRepositoryActor = system.actorOf(BookingsEventLogRepositoryActor.props, "reservationsEventLogRepositoryActor")
      val reservationId = UUID.randomUUID()
      val event = ReservationCreated(
        reservationId = reservationId,
        eventDateTime = ZonedDateTime.now(),
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
