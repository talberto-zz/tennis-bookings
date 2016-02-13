package controllers

import com.typesafe.config.impl.ConfigImpl
import org.scalatest.{BeforeAndAfterEach, Suite}

trait WithConfiguredApp extends BeforeAndAfterEach {

  this: Suite with WithDB with WithSite with WithWebDriver =>

  override protected def beforeEach: Unit = {
    super.beforeEach
    // Dirty hack to force reloading typesafe config from system properties
    ConfigImpl.reloadSystemPropertiesConfig()
    Thread.sleep(5000)
  }
}