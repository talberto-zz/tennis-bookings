package models

import org.openqa.selenium.WebDriver

import com.google.inject.AbstractModule

import net.codingwell.scalaguice.ScalaModule

import play.api.Play
import play.api.Play._
import play.api.Configuration

class BookingsModule extends AbstractModule with ScalaModule {

  def configure {
    if(Play.isDev) {
      bind[WebDriverFactory].to[DefaultWebDriverFactory]
    } else if(Play.isProd) {
      bind[WebDriverFactory].to[RemoteWebDriverFactory]
    }
    bind[WebDriver].toProvider[WebDriverProvider]
    bind[Configuration].toProvider[ConfigurationProvider]
  }
}
