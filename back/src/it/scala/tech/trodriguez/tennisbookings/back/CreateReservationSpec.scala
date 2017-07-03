package tech.trodriguez.tennisbookings.back

import java.time.ZonedDateTime

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import play.api.http.{ContentTypes, HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import play.api.test.WsTestClient
import tech.trodriguez.tennisbookings.back.controllers.BookingRequest
import tech.trodriguez.tennisbookings.back.docker.WithConfiguredServerPerTest
import tech.trodriguez.tennisbookings.back.util.ConfigurableScaleFactor

/**
  * Created by trodriguez on 11/02/16.
  */
class CreateReservationSpec extends WordSpec
  with Matchers
  with OptionValues
  with ScalaFutures
  with WsTestClient
  with GivenWhenThen
  with WithConfiguredServerPerTest
  with ConfigurableScaleFactor {

  "TennisReservations app" must {
    "create a new reservation" in {
      Given("A request to create a new reservation")
      val reservationRequest = BookingRequest(ZonedDateTime.now(), 12)

      When("We get the response")
      val eventualCreationResponse = wsCall(routes.BookingsController.create())
        .withHttpHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
        .post(Json.toJson(reservationRequest))

      Then("The response status is OK and it points to the newly created request")
      val creationResponse = eventualCreationResponse.futureValue
      creationResponse.status should be(Status.CREATED)
      creationResponse.header(HeaderNames.LOCATION) should not be empty

      When("We get the newly created reservation")
      val resourceUrl = creationResponse.header(HeaderNames.LOCATION).get
      val findResponse = wsUrl(resourceUrl).get().futureValue

      Then("The reservation contains the same date and court that we requested")
      findResponse.status should be(Status.OK)
      findResponse.header(HeaderNames.CONTENT_TYPE).value should be(ContentTypes.JSON)
      val reservation = findResponse.json.as[Reservation]
      reservation.dateTime should be(reservationRequest.dateTime)
      reservation.court should be(reservationRequest.court)
    }
  }
}
