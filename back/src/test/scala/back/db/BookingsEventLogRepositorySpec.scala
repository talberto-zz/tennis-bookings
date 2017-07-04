package back.db

import java.time.ZonedDateTime
import java.util.UUID

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import tech.trodriguez.tennisbookings.back.actor.BookingAggregateActor.BookingCreated
import tech.trodriguez.tennisbookings.back.db.BookingsEventLogRepository

/**
  * Created by trodriguez on 11/02/16.
  */
class BookingsEventLogRepositorySpec extends WordSpec
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen {

  import play.api.libs.concurrent.Execution.Implicits._

  "BookingsEventLogRepository" must {
    "save an event" in {
      Given("A booking event")
      val bookingId = UUID.randomUUID()
      val event = BookingCreated(
        bookingId = bookingId,
        eventDateTime = ZonedDateTime.now(),
        dateTime = ZonedDateTime.now(),
        court = 1
      )

      When("We save it")
      val eventualUnit = BookingsEventLogRepository.add(event)
      eventualUnit.futureValue

      Then("We are able to get it later")
      val eventualEvents = BookingsEventLogRepository.findAllEvents(bookingId)
      val events = eventualEvents.futureValue

      events should have size 1
      events.head should equal(event)
    }
  }
}
