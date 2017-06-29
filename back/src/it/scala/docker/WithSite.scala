package docker

import config.TestConstants.Boot2Docker
import docker.Docker._
import org.scalatest.{BeforeAndAfterEach, Suite}

trait WithSite extends BeforeAndAfterEach {
  this: Suite =>

  private var siteContainerId: ContainerId = _

  var siteHttpPort: Int = _

  override protected def beforeEach: Unit = {
    siteContainerId = runImage("talberto/tennis-reservations-site")
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
