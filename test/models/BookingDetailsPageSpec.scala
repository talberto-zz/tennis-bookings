package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class BookingDetailsPageSpec extends Specification {

  "The 'ChoosePartnerPage'" should {
    "return to the 'CourtsTablePage' when the partner has been chosen" in new BookingDetailsPageSpecBeforeAfter {
      bookingDetailsPage.choosePartner
      bookingDetailsPage.confirmBooking
      bookingDetailsPage.driver.getCurrentUrl must equalTo(destUrl)
    }
    
    "recognize it's page" in new BookingDetailsPageSpecBeforeAfter {
      BookingDetailsPage.isCurrentPage(driver) must equalTo(true)
      driver.get("http://www.google.com")
      BookingDetailsPage.isCurrentPage(driver) must equalTo(false)
    }
  }
}

trait BookingDetailsPageSpecBeforeAfter extends After {
  val url = "http://localhost:8080/booking_details/"
  val destUrl = "http://localhost:8080/courts_table/"
  val conf = new BookingDetailsPageConf
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  driver.get(url)
  val bookingDetailsPage = BookingDetailsPage(driver, conf)
  
  def after = {
    bookingDetailsPage.driver.close
  }
}