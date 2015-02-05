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
class CourtsTablePageSpec extends Specification {
  
  "The 'CourtsTablePage'" should {
    "open the 'ChoosePartnerPage' when clicked twice over a court" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(17), court = 15)
      courtsTablePage.book(booking)
      
      courtsTablePage.driver.getCurrentUrl must equalTo(destUrl)
    }
    
    "'canBook' returns false when the book isn't possible" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(18), court = 12)
      courtsTablePage.canBook(booking) must equalTo(false)
    }
    
    "'canBook' returns true when the book is possible" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(17), court = 15)
      courtsTablePage.canBook(booking) must equalTo(true)
    }
  }
}

trait CourtsTablePageSpecBeforeAfter extends After {
  // Setup the LoginPage 
  val app = FakeApplication()
  val conf: Configuration = app.configuration
  val url = conf.getString("courtsTablePage.url").get
  val destUrl = conf.getString("choosePartnerPage.url").get
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  driver.get(url)
  val courtsTablePage = CourtsTablePage(driver, CourtsTablePageConf(conf))
  
  def after = {
    courtsTablePage.driver.close
  }
}