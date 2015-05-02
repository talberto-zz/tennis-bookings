package models.site

import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.specs2.mutable._
import org.specs2.runner._

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
  val url = "http://localhost:8080/bookings_reminder/"
  val conf = BookingsReminderPageConf()
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  val bookingsReminderPage = BookingsReminderPage(driver, conf)
  driver.get(url)
  def after = {
    driver.close
  }
}