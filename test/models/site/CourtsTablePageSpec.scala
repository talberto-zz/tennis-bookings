package models.site

import models.db.Booking
import org.joda.time.DateTime
import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class CourtsTablePageSpec extends Specification {
  
  "The 'CourtsTablePage'" should {
    "open the 'BookingDetailsPage' when clicked twice over a court" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(17), court = 15)
      courtsTablePage.book(booking)
      
      BookingDetailsPage.isCurrentPage(driver) must equalTo(true)
    }
    
    "'canBook' returns false when the court is already booked" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(18), court = 12)
      courtsTablePage.canBook(booking) must equalTo(false)
    }
    
    "'canBook' returns true when the court isn't already booked" in new CourtsTablePageSpecBeforeAfter {
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(17), court = 15)
      courtsTablePage.canBook(booking) must equalTo(true)
    }
    
    "recognize it's page" in new CourtsTablePageSpecBeforeAfter {
      CourtsTablePage.isCurrentPage(driver) must equalTo(true)
      driver.get("http://www.google.com")
      CourtsTablePage.isCurrentPage(driver) must equalTo(false)
    }
    
    "throws exception when bookings limit is reached" in new CourtsTablePageSpecBeforeAfter {
      driver.get("http://localhost:8080/bookings_limit_reached")
      val booking = Booking(dateTime = DateTime.now().withHourOfDay(20), court = 15)
      courtsTablePage.book(booking) must throwA[BookingsLimitReachedException]
    }
  }
}

trait CourtsTablePageSpecBeforeAfter extends After {
  val url = "http://localhost:8080/courts_table/"
  val conf = CourtsTablePageConf()
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  driver.get(url)
  val courtsTablePage = CourtsTablePage(driver, conf)
  
  def after = {
    courtsTablePage.driver.close
  }
}