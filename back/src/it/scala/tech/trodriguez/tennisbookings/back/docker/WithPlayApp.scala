package tech.trodriguez.tennisbookings.back.docker

import org.scalatest.{BeforeAndAfterEach, Suite}
import tech.trodriguez.tennisbookings.back.docker.Docker.{ContainerId, findExposedPort, removeContainer, runImage}
import tech.trodriguez.tennisbookings.back.config.TestConstants._

trait WithPlayApp extends BeforeAndAfterEach {
  this: Suite =>

  private var appContainerId: ContainerId = _

  var appContainerPort: Int = _

  override protected def beforeEach: Unit = {
    appContainerId = runImage("selenium/standalone-chrome")
    // Get exposed port
    appContainerPort = findExposedPort(9000, 4444)
    // Set system properties
    sys.props ++= Map(
      "webdriver.host" -> Boot2Docker,
      "webdriver.port" -> appContainerPort.toString
    )
    super.beforeEach
  }

  override protected def afterEach: Unit = {
    try {
      super.afterEach
    } finally {
      removeContainer(appContainerId)
    }
  }
}
