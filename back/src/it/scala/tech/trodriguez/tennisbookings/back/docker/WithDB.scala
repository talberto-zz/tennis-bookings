package tech.trodriguez.tennisbookings.back.docker

import org.scalatest.{BeforeAndAfterEach, Suite}
import tech.trodriguez.tennisbookings.back.config.TestConstants._
import tech.trodriguez.tennisbookings.back.docker.Docker._

trait WithDB extends BeforeAndAfterEach {
  this: Suite =>

  private var dbContainerId: ContainerId = _

  var dbPort: Int = _

  override protected def beforeEach: Unit = {
    dbContainerId = runImage("talberto/tennis-bookings-db")
    // Get exposed port
    dbPort = findExposedPort(dbContainerId, 5432)
    // Set system properties
    sys.props ++= Map(
      "reference.db.host" -> Boot2Docker,
      "reference.db.port" -> dbPort.toString
    )
    super.beforeEach
  }

  override protected def afterEach: Unit = {
    try {
      super.afterEach
    } finally {
      removeContainer(dbContainerId)
    }
  }
}
