package models

import java.util.concurrent.TimeUnit

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Days

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import play.api.Logger
import play.api.Configuration

class TennisSite {
  self: WithConfiguration =>
  
  val logger = Logger(getClass)
  lazy val driver: WebDriver = WebDriverFactory.createDriver(conf)
  lazy val loginPage = LoginPage(driver, loginPageConf)
  lazy val bookingsReminderPage = BookingsReminderPage(driver, bookingsReminderPageConf)
  lazy val courtsTablePage = CourtsTablePage(driver, courtsTablePageConf)
  lazy val bookingDetailsPage = BookingDetailsPage(driver, choosePartnerPageConf)
  
  def book(booking: Booking) = {
    logger.trace(s"book($booking)")
    loginPage.get
    loginPage.doLogin
    
    if(bookingsReminderPage.isCurrentPage) {
      bookingsReminderPage.goToCourtsTable
    }
    courtsTablePage.book(booking)
    bookingDetailsPage.choosePartner
  }
}

object TennisSite {
  /** We can start booking from 9:00 AM */
  val BookingStartingHour = new LocalTime(9, 0, 0, 0)
  /** Number of days, starting from today, that we can book */
  val DaysOfDifference = Days.TWO
  
  def apply(configuration: Configuration): TennisSite = new TennisSite with WithConfiguration { lazy val conf = configuration }
  
  def canBookToday(booking: Booking) = Days.daysBetween(LocalDate.now, booking.date).isLessThan(TennisSite.DaysOfDifference.plus(Days.ONE))
}

trait WithConfiguration {
  def conf: Configuration

  val loginPageConf: LoginPageConf = LoginPageConf(conf)
  val bookingsReminderPageConf: BookingsReminderPageConf = BookingsReminderPageConf(conf)
  val courtsTablePageConf: CourtsTablePageConf = CourtsTablePageConf(conf)
  val choosePartnerPageConf: BookingDetailsPageConf = BookingDetailsPageConf(conf)
}
