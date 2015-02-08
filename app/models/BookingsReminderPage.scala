package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import play.api.Configuration
import play.api.Logger

import scala.collection.JavaConversions._

class BookingsReminderPage private(val driver: WebDriver, val conf: BookingsReminderPageConf) extends Page {
  val logger: Logger = Logger(this.getClass)
  
  def goToCourtsTable = {
    logger.trace(s"goToCourtsTable()")
    val buttons = driver.findElements(By.cssSelector("#workpage button")).toList
    buttons(1).click
  }
  
  def isCurrentPage = BookingsReminderPage.isCurrentPage(driver)
}

object BookingsReminderPage extends PageObject {
  def apply(driver: WebDriver, conf: BookingsReminderPageConf) = new BookingsReminderPage(driver, conf)
  
  def isCurrentPage(driver: WebDriver): Boolean = {
    driver.findElements(By.id("liste_reservation")).nonEmpty
  }
}

case class BookingsReminderPageConf(val url: String)

object BookingsReminderPageConf {
  def apply(conf: Configuration): BookingsReminderPageConf = BookingsReminderPageConf(conf.getString("bookingsReminderPage.url").get)
}
