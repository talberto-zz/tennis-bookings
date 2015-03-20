package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.joda.time.DateTime

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class TennisSiteSpec extends Specification {
  "The 'TennisSite'" should {
    "book a court successfully" in new TennisSiteSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(17), court = 15)
      tennisSite.book(booking)
      success
    }
  }
}

trait TennisSiteSpecBeforeAfter extends BeforeAfter {
  val app = FakeApplication()
  val appConf: Configuration = app.configuration
  val webDriverFactory = new DefaultWebDriverFactory(appConf)
  val loginUrl = "http://localhost:8080/login/"
  val tennisSite: TennisSite = new TennisSite(webDriverFactory, appConf)
  
  def before = {
    
  }
  
  def after = {
    tennisSite.driver.close
  }
}
