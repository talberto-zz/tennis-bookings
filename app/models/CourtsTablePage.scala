package models

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import play.api.Logger
import play.api.Configuration

import scala.collection.JavaConversions._

class CourtsTablePage private(val driver: WebDriver, val conf: CourtsTablePageConf) extends Page {
  val logger: Logger = Logger(this.getClass)
  val actions: Actions = new Actions(driver)
  
  def book(booking: Booking) = {
    logger.trace(s"book($booking)")
    logger.debug(s"Trying to book court [${booking.court}] at hour [${booking.time}]")
    val courtElem = findCourt(booking)
    // Make double click
    logger.debug(s"Double clicking on element [identified by css selector [$courtElem]")
    actions.doubleClick(courtElem).perform()
  }
  
  /**
   * Determines if we can book or not
   */
  def canBook(booking: Booking) = {
    val courtElem = findCourt(booking)
    val backGroundColor = courtElem.getCssValue("background-color")
    logger.debug(s"background-color = [$backGroundColor]")
    backGroundColor == "rgba(153, 255, 153, 1)"
  }
  
  protected def findCourt(booking: Booking): WebElement = {
    logger.trace(s"findCourt($booking)")
    logger.debug(s"Trying find the court [${booking.court}] at hour [${booking.time}]")
    val courtNo = booking.court + 1
    val hourNo = booking.time.getHourOfDay()
    
    // Find the box representing the court
    val selector = s"${hourNo}_0_${courtNo}"
    logger.debug(s"Searching element with selector [$selector]")
    driver.findElement(By.id(selector))
  }
  
  def isCurrentPage: Boolean = CourtsTablePage.isCurrentPage(driver)
}

object CourtsTablePage extends PageObject {
  def apply(driver: WebDriver, conf: CourtsTablePageConf) = new CourtsTablePage(driver, conf)
  
  def isCurrentPage(driver: WebDriver): Boolean = {
    driver.findElements(By.id("workpage")).nonEmpty
  }
}

case class CourtsTablePageConf(val url: String)

object CourtsTablePageConf {
  def apply(conf: Configuration): CourtsTablePageConf = CourtsTablePageConf(conf.getString("courtsTablePage.url").get)
}
