package back.actor

import java.time.ZonedDateTime
import java.util.UUID

import akka.testkit.{TestKit, TestProbe}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import play.api.inject.guice.GuiceApplicationBuilder
import tech.trodriguez.tennisbookings.back.actor.BookingAggregateActor.BookingCreated
import tech.trodriguez.tennisbookings.back.actor.BookingsEventLogRepositoryActor

import scala.concurrent.duration._

class BookingsEventLogRepositoryActorSpec extends WordSpec
  with BeforeAndAfterAll
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen {

  lazy val application = new GuiceApplicationBuilder().build()
  lazy val injector = application.injector
  implicit lazy val system = application.actorSystem

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "BookingsEventLogRepositoryActor" must {
    import BookingsEventLogRepositoryActor._

    "save an event" in {
      val probe = TestProbe()
      implicit val sender = probe.ref

      Given("A booking event")
      val bookingsEventLogRepositoryActor = system.actorOf(BookingsEventLogRepositoryActor.props, "bookingsEventLogRepositoryActor")
      val bookingId = UUID.randomUUID()
      val event = BookingCreated(
        bookingId = bookingId,
        eventDateTime = ZonedDateTime.now(),
        dateTime = ZonedDateTime.now(),
        court = 1
      )

      When("We save it")
      probe.within(5 seconds) {
        bookingsEventLogRepositoryActor ! SaveEvent(event)
        probe.expectMsg(EventSaved(event))
      }

      Then("We are able to get it later")
      probe.within(5 seconds) {
        bookingsEventLogRepositoryActor ! FindEvents(bookingId)
        probe.expectMsg(EventsFound(Seq(event)))
      }
    }
  }
}
