package models

import org.joda.time.DateTime

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2._
import org.specs2.mock._
import org.specs2.runner._

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class BookingsManagerSpec extends Specification { def is = s2"""
  The 'BookingsManager'
    tries to book immediately if the booking date is within the next 3 days      ${c().e1}
    schedules the actual booking for later if the date is later than 3 days     ${c().e2}
  """

  case class c() extends Mockito {
    val tennisSite = mock[TennisSite]
    val bookingsRepository = mock[BookingsRepository]
    val bookingsManager = BookingsManager(bookingsRepository, tennisSite)
    
    def e1 = {
      pending
    }
    
    def e2 = {
      pending
    }
  }
}
