package models.site

import com.google.inject.{Inject, Provider}
import org.openqa.selenium.WebDriver

class WebDriverProvider @Inject() (val webDriverFactory: WebDriverFactory) extends Provider[WebDriver] {

  def get: WebDriver = {
    webDriverFactory.createDriver
  }
}
