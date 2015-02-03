package models

import org.joda.time.LocalDate

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
    val bookingsScheduler = mock[BookingsScheduler]
    val bookingsManager = BookingsManager(tennisSite, bookingsScheduler)
    
    def e1 = {
      val date = LocalDate.now()
      bookingsManager.book(date, Hours.HOUR_17, Courts.COURT_15)
      there was one(tennisSite).book(date, Hours.HOUR_17, Courts.COURT_15)
      there was no(bookingsScheduler).scheduleBooking(any[BookingsManager], any[LocalDate], any[Hours.Hours], any[Courts.Courts])
    }
    
    def e2 = {
      val date = LocalDate.now().plusDays(4)
      bookingsManager.book(date, Hours.HOUR_17, Courts.COURT_15)
      there was no(tennisSite).book(any[LocalDate], any[Hours.Hours], any[Courts.Courts])
      there was one(bookingsScheduler).scheduleBooking(bookingsManager, date, Hours.HOUR_17, Courts.COURT_15)
    }
  }
}
