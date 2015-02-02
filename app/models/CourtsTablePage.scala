package models

import play.api.Logger
import play.api.Configuration

import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

class CourtsTablePage private(val driver: WebDriver, val conf: CourtsTablePageConf) {
  val logger: Logger = Logger(this.getClass)
  val actions: Actions = new Actions(driver)
  
  def book(court: Courts.Courts, hour: Hours.Hours) = {
    logger.trace(s"book($court, $hour)")
    logger.debug(s"Trying to book court [$court] at hour [$hour]")
    val courtNo = court.toString().stripPrefix("COURT_").toInt + 1
    val hourNo = hour.toString().stripPrefix("HOUR_").toInt
    
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

/**
 * The possible courts
 */
object Courts extends Enumeration {
  type Courts = Value
  val COURT_1, 
    COURT_2, 
    COURT_3, 
    COURT_4, 
    COURT_5, 
    COURT_6, 
    COURT_7, 
    COURT_8, 
    COURT_9, 
    COURT_10, 
    COURT_11, 
    COURT_12, 
    COURT_13, 
    COURT_14, 
    COURT_15, 
    COURT_16 = Value 
    
  def unapply(v: Int) = Courts(v)
}

/**
 * The possible hours
 */
object Hours extends Enumeration {
  type Hours = Value
  val HOUR_8,
    HOUR_9,
    HOUR_10,
    HOUR_11,
    HOUR_12,
    HOUR_13,
    HOUR_14,
    HOUR_15,
    HOUR_16,
    HOUR_17,
    HOUR_18,
    HOUR_19,
    HOUR_20,
    HOUR_21 = Value
    
  def unapply(v: Int) = Hours(v)
}