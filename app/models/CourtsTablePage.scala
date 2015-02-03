package models

import play.api.Logger
import play.api.Configuration

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

class CourtsTablePage private(val driver: WebDriver, val conf: CourtsTablePageConf) {
  val logger: Logger = Logger(this.getClass)
  val actions: Actions = new Actions(driver)
  
  def book(booking: Booking) = {
    logger.trace(s"book($booking)")
    logger.debug(s"Trying to book court [${booking.court}] at hour [${booking.time}]")
    val courtNo = booking.court + 1
    val hourNo = booking.time.getHourOfDay()
    
    // Find the box representing the court
    val selector = s"${hourNo}_0_${courtNo}"
    logger.debug(s"Double clicking on element identified by css selector [$selector]")
    val courtElem = driver.findElement(By.id(selector))
    // Make double click
    actions.doubleClick(courtElem).perform()
  }
}

object CourtsTablePage {
  def apply(driver: WebDriver, conf: CourtsTablePageConf) = new CourtsTablePage(driver, conf)
}

case class CourtsTablePageConf(val url: String)

object CourtsTablePageConf {
  def apply(conf: Configuration): CourtsTablePageConf = CourtsTablePageConf(conf.getString("courtsTablePage.url").get)
}
