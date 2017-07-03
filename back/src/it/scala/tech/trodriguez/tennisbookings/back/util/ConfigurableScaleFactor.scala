package tech.trodriguez.tennisbookings.back.util

import org.scalatest.concurrent.ScaledTimeSpans
import tech.trodriguez.tennisbookings.back.config.TestConstants

trait ConfigurableScaleFactor extends ScaledTimeSpans {

  override def spanScaleFactor: Double = TestConstants.ScaleFactor
}
