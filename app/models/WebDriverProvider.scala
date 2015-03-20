package models

import org.openqa.selenium.WebDriver

import com.google.inject.Inject
import com.google.inject.Provider

class WebDriverProvider @Inject() (val webDriverFactory: WebDriverFactory) extends Provider[WebDriver] {

  def get: WebDriver = {
    webDriverFactory.createDriver
  }
}
