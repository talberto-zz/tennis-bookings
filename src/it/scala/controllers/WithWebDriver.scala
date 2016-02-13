package controllers

import controllers.Docker._
import controllers.TestConstants.Boot2Docker
import org.scalatest._

trait WithWebDriver extends BeforeAndAfterEach {
  this: Suite =>

  private var webdriverContainerId: ContainerId = _

  var webdriverPort: Int = _

  override protected def beforeEach: Unit = {
    webdriverContainerId = runImage("selenium/standalone-chrome")
    // Get exposed port
    webdriverPort = findExposedPort(webdriverContainerId, 4444)
    // Set system properties
    sys.props ++= Map(
      "webdriver.host" -> Boot2Docker,
      "webdriver.port" -> webdriverPort.toString
    )
    super.beforeEach
  }

  override protected def afterEach: Unit = {
    try {
      super.afterEach
    } finally {
      removeContainer(webdriverContainerId)
    }
  }
}
