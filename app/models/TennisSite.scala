package models

import org.joda.time.LocalDate

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import play.api.Logger
import play.api.Configuration

class TennisSite {
  self: WithConfiguration =>
  
  val logger = Logger(getClass)
  lazy val driver: WebDriver = new ChromeDriver
  lazy val loginPage = LoginPage(driver, loginPageConf)
  lazy val courtsTablePage = CourtsTablePage(driver, courtsTablePageConf)
  lazy val choosePartnerPage = ChoosePartnerPage(driver, choosePartnerPageConf)
  
  def book(booking: Booking) = {
    logger.trace(s"book($booking)")
    loginPage.get
    loginPage.doLogin
    courtsTablePage.book(booking)
    choosePartnerPage.choose
  }
}

object TennisSite {
  def apply(configuration: Configuration): TennisSite = new TennisSite with WithConfiguration { lazy val conf = configuration }
}

trait WithConfiguration {
  def conf: Configuration
  val loginPageConf: LoginPageConf = LoginPageConf(conf)
  val courtsTablePageConf: CourtsTablePageConf = CourtsTablePageConf(conf)
  val choosePartnerPageConf: ChoosePartnerPageConf = ChoosePartnerPageConf(conf)
}
