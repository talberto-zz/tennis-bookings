package models.db

import java.time.ZonedDateTime
import java.util.UUID

import docker.WithConfiguredServerPerTest
import models.actor.ReservationAggregateActor.ReservationCreated
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import util.ConfigurableScaleFactor

/**
  * Created by trodriguez on 11/02/16.
  */
class ReservationsEventLogRepositorySpec extends WordSpec
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen
  with WithConfiguredServerPerTest
  with ConfigurableScaleFactor {

  import play.api.libs.concurrent.Execution.Implicits._

  "ReservationsEventLogRepository" must {
    "save an event" in {
      Given("A reservation event")
      val reservationId = UUID.randomUUID()
      val event = ReservationCreated(
        reservationId = reservationId,
        eventDateTime = ZonedDateTime.now(),
        dateTime = ZonedDateTime.now(),
        court = 1
      )

      When("We save it")
      val eventualUnit = ReservationsEventLogRepository.add(event)
      eventualUnit.futureValue

      Then("We are able to get it later")
      val eventualEvents = ReservationsEventLogRepository.findAllEvents(reservationId)
      val events = eventualEvents.futureValue

      events should have size 1
      events.head should equal(event)
    }
  }
}
