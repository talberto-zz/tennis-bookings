package tech.trodriguez.tennisbookings.back.docker

import org.scalatest.TestSuite
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
  * Created by trodriguez on 12/02/16.
  */
trait WithConfiguredServerPerTest extends WithPlayApp
  with WithSite
  with WithDB
  with WithWebDriver
  with WithConfiguredApp {

  this: TestSuite =>

}
