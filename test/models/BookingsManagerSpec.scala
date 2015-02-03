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
    val bookingsScheduler = mock[BookingsScheduler]
    val bookingsManager = BookingsManager(tennisSite, bookingsScheduler)
    
    def e1 = {
      val booking = Booking(dateTime = DateTime.now(), court = 15)
      bookingsManager.book(booking)
      there was one(tennisSite).book(booking)
      there was no(bookingsScheduler).scheduleBooking(any[BookingsManager], any[Booking])
    }
    
    def e2 = {
      val booking = Booking(dateTime = DateTime.now().plusDays(4), court = 15)
      bookingsManager.book(booking)
      there was no(tennisSite).book(any[Booking])
      there was one(bookingsScheduler).scheduleBooking(bookingsManager, booking)
    }
  }
}
