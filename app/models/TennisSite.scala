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
  
  def book(date: LocalDate = LocalDate.now, hour: Hours.Hours, court: Courts.Courts) = {
    logger.trace(s"book($date, $hour, $court)")
    loginPage.get
    loginPage.doLogin
    courtsTablePage.book(court, hour)
    choosePartnerPage.choose
  }
}

trait WithConfiguration {
  def conf: Configuration
  val loginPageConf: LoginPageConf = LoginPageConf(conf)
  val courtsTablePageConf: CourtsTablePageConf = CourtsTablePageConf(conf)
  val choosePartnerPageConf: ChoosePartnerPageConf = ChoosePartnerPageConf(conf)
}

class DefaultTennisSite(override val conf: Configuration) extends TennisSite with WithConfiguration {
  
}
