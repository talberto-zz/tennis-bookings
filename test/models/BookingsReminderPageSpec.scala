package models

import org.joda.time.DateTime

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class BookingsReminderPageSpec extends Specification {

  "The 'BookingsReminderPage'" should {
    "when closed we arrive to CourtsTablePage" in new BookingsReminderPageSpecBeforeAfter {
      bookingsReminderPage.goToCourtsTable
      CourtsTablePage.isCurrentPage(driver) must equalTo(true)
    }
    
    "recognize it's page" in new BookingsReminderPageSpecBeforeAfter {
      BookingsReminderPage.isCurrentPage(driver) must equalTo(true)
      driver.get("http://www.google.com")
      BookingsReminderPage.isCurrentPage(driver) must equalTo(false)
    }
  }
}

trait BookingsReminderPageSpecBeforeAfter extends After {
  // Setup the LoginPage 
  val app = FakeApplication()
  val appConf: Configuration = app.configuration
  val conf = BookingsReminderPageConf(appConf)
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  val bookingsReminderPage = BookingsReminderPage(driver, conf)
  driver.get(conf.url)
  def after = {
    driver.close
  }
}