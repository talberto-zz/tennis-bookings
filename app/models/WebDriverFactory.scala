package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.Platform
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import java.util.concurrent.TimeUnit
import java.net.URL

import play.api.Configuration

object WebDriverFactory {
  def createDriver(conf: Configuration): WebDriver = {
    val urlString: String = conf.getString("webDriver.url").get
    val url: URL = new URL(urlString)
    val browser: String = conf.getString("webDriver.browser").get
    val implicitTimeout = conf.getInt("tennisSite.implicitTimeout").get
    val pageLoadTimeout = conf.getInt("tennisSite.pageLoadTimeout").get
  
    val driver = new RemoteWebDriver(url, new DesiredCapabilities(browser, "", Platform.ANY))
    driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
    driver.manage().timeouts().implicitlyWait(implicitTimeout, TimeUnit.SECONDS);
    driver
  }
}
