package controllers

import java.time.ZonedDateTime

import models.db.ReservationRequest
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.WsScalaTestClient
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.Json

/**
  * Created by trodriguez on 11/02/16.
  */
class ReservationCreationSpec extends WordSpec
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsScalaTestClient
  with GivenWhenThen
  with WithConfiguredServerPerTest {

  "TennisReservations app" must {
    "create a new reservation" in {
      Given("A request to create a new reservation")
      val reservationRequest = ReservationRequest(ZonedDateTime.now(), 12)

      When("We get the response")
      val eventualResponse = wsCall(routes.ReservationsController.create())
        .post(Json.toJson(reservationRequest))

      Then("The response status is OK and it points to the newly created request")
      val response = eventualResponse.futureValue
      response.status should be (Status.CREATED)
      response.header(HeaderNames.LOCATION) should not be empty
    }
  }
}
