package models.site

import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{Platform, WebDriver}
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
    val urlString: String = conf.getString("webdriver.url").get
    val url: URL = new URL(urlString)
    val browser: String = conf.getString("webdriver.browser").get
    val implicitTimeout = conf.getInt("webdriver.implicitTimeout").get
    val pageLoadTimeout = conf.getInt("webdriver.pageLoadTimeout").get
  
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
