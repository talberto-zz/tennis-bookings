package tech.trodriguez.tennisbookings.back.docker

import Docker._
import org.scalatest.{BeforeAndAfterEach, Suite}
import tech.trodriguez.tennisbookings.back.config.TestConstants._

trait WithSite extends BeforeAndAfterEach {
  this: Suite =>

  private var siteContainerId: ContainerId = _

  var siteHttpPort: Int = _

  override protected def beforeEach: Unit = {
    siteContainerId = runImage("talberto/tennis-bookings-site")
    // Get exposed port
    siteHttpPort = findExposedPort(siteContainerId, 8080)
    // Set system properties
    sys.props ++= Map(
      "loginPage.url" -> s"http://$Boot2Docker:$siteHttpPort"
    )
    super.beforeEach
  }

  override protected def afterEach: Unit = {
    try {
      super.afterEach
    } finally {
      removeContainer(siteContainerId)
    }
  }
}
