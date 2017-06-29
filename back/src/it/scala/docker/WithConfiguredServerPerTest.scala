package docker

import org.scalatest.Suite
import org.scalatestplus.play.OneServerPerTest

/**
  * Created by trodriguez on 12/02/16.
  */
trait WithConfiguredServerPerTest extends OneServerPerTest
  with WithSite
  with WithDB
  with WithWebDriver
  with WithConfiguredApp {

  this: Suite =>

}
