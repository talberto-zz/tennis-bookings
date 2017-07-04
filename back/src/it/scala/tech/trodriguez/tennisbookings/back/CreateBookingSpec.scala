package tech.trodriguez.tennisbookings.back

import java.time.ZonedDateTime

import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.{DefaultKubernetesClient, LocalPortForward}
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import play.api.http.{ContentTypes, HeaderNames, MimeTypes, Status}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSBodyWritables._
import play.api.libs.ws.WSClient
import tech.trodriguez.tennisbookings.back.controllers.{BookingRequest, routes}

import scala.concurrent.duration._
import scala.io.Source
import scala.sys.process.Process
import scala.util.Random

/**
  * Created by trodriguez on 11/02/16.
  */
class CreateBookingSpec extends WordSpec
  with OptionValues
  with ScalaFutures
  with Eventually
  with Matchers
  with BeforeAndAfterEach
  with GivenWhenThen {

  val Alphabet = ('a' to 'z').mkString
  val UseKubectl = true
  val client = new DefaultKubernetesClient()
  val wsClient = new GuiceApplicationBuilder().injector().instanceOf[WSClient]
  val podName = "tennis-bookings-" + Stream.continually(Random.nextInt(Alphabet.length)).map(Alphabet(_)).take(10).mkString
  var appPod: Pod = _
  var process: Process = _
  var portForwarder: LocalPortForward = _
  val appPort = 9000

  override protected def beforeEach(): Unit = {
    appPod = createAppPod(client)

    if(UseKubectl) {
      import scala.sys.process._
      process = s"kubectl port-forward ${appPod.getMetadata.getName} $appPort:$appPort" run ProcessLogger(_ => ())
    } else {
      portForwarder = client.pods().withName(appPod.getMetadata.getName).portForward(appPort, appPort)
    }

    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    try {
      super.afterEach()
    } finally {
      client.pods().delete(appPod)
      if(UseKubectl) {
        process.destroy()
      } else {
        portForwarder.close()
      }
    }
  }

  "TennisBookings app" must {
    "create a new booking" in {
      Given("A request to create a new booking")
      val bookingRequest = BookingRequest(ZonedDateTime.now(), 12)

      When("We get the response")
      val eventualCreationResponse = wsClient.url(s"http://localhost:${appPort}${routes.BookingsController.create().url}")
        .withHttpHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON)
        .post(Json.toJson(bookingRequest))

      Then("The response status is OK and it points to the newly created request")
      val creationResponse = eventualCreationResponse.futureValue(timeout(5 seconds), interval(500 millis))
      creationResponse.status should be(Status.CREATED)
      creationResponse.header(HeaderNames.LOCATION) should not be empty

      When("We get the newly created booking")
      val resourceUrl = creationResponse.header(HeaderNames.LOCATION).get
      val findResponse = wsClient.url(resourceUrl).get().futureValue

      Then("The booking contains the same date and court that we requested")
      findResponse.status should be(Status.OK)
      findResponse.header(HeaderNames.CONTENT_TYPE).value should be(ContentTypes.JSON)
      val booking = findResponse.json.as[Booking]
      booking.dateTime should be(bookingRequest.dateTime)
      booking.court should be(bookingRequest.court)
    }
  }

  private def createAppPod(client: DefaultKubernetesClient): Pod = {
    // Create configmap
    val appConf = Source.fromResource("k8s/application.conf").mkString
    val logConf = Source.fromResource("k8s/logback.xml").mkString
    client.configMaps().inNamespace("default").createOrReplaceWithNew()
      .withNewMetadata()
      .withName("tennis-bookings-config")
      .endMetadata()
      .addToData("application.conf", appConf)
      .addToData("logback.xml", logConf)
      .done()

    // Create the pod
    val appPodStream = this.getClass.getResourceAsStream("/k8s/tennis-bookings.yaml")
    val appPod = client.pods().load(appPodStream).get()
    appPod.getMetadata.setName(podName)
    client.pods().inNamespace("default").createOrReplace(appPod)

    // Wait until the pod is running
    eventually(timeout(10 seconds), interval(1 second)) {
      val pod = client.pods().inNamespace("default").withName(appPod.getMetadata.getName).get()

      pod.getStatus.getPhase should be("Running")

      pod
    }
  }
}
