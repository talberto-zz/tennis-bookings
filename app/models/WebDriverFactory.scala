package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.Platform
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import org.openqa.selenium.chrome.ChromeDriver

import java.util.concurrent.TimeUnit
import java.net.URL

import javax.inject.Inject
import javax.inject.Singleton

import play.api.Configuration

/**
 * Factory for creating new [[org.openqa.selenium.WebDriver]]
 */
trait WebDriverFactory {
  def createDriver: WebDriver
}

@Singleton
class RemoteWebDriverFactory @Inject() (val conf: Configuration) extends WebDriverFactory {
  def createDriver: WebDriver = {
    val urlString: String = conf.getString("webDriver.url").get
    val url: URL = new URL(urlString)
    val browser: String = conf.getString("webDriver.browser").get
    val implicitTimeout = conf.getInt("webDriver.implicitTimeout").get
    val pageLoadTimeout = conf.getInt("webDriver.pageLoadTimeout").get
  
    val driver = new RemoteWebDriver(url, new DesiredCapabilities(browser, "", Platform.ANY))
    driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
    driver.manage().timeouts().implicitlyWait(implicitTimeout, TimeUnit.SECONDS);
    driver
  }
}

@Singleton
class DefaultWebDriverFactory @Inject() (val conf: Configuration) extends WebDriverFactory {
  def createDriver: WebDriver = {
    new ChromeDriver
  }
}
