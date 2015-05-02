package models.site

import javax.inject.{Inject, Singleton}
import models.db.Booking
import org.joda.time.{Days, LocalDate, LocalTime}
import play.api.Logger

@Singleton
class TennisSite @Inject() (val loginPage: LoginPage, val bookingsReminderPage: BookingsReminderPage, val courtsTablePage: CourtsTablePage, val bookingDetailsPage: BookingDetailsPage)  {
  
  val logger = Logger(getClass)
  
  def book(booking: Booking) = {
    this.synchronized {
      logger.trace(s"book($booking)")
      loginPage.get
      loginPage.doLogin

      if (bookingsReminderPage.isCurrentPage) {
        bookingsReminderPage.goToCourtsTable
      }

      courtsTablePage.book(booking)
      bookingDetailsPage.choosePartner
    }
  }
  
  def canBookToday(booking: Booking) = Days.daysBetween(LocalDate.now, booking.date).isLessThan(TennisSite.DaysOfDifference.plus(Days.ONE))
}

object TennisSite {
  /** We can start booking from 9:00 AM */
  val BookingStartingHour = new LocalTime(9, 0, 0, 0)
  /** Number of days, starting from today, that we can book */
  val DaysOfDifference = Days.TWO
}
