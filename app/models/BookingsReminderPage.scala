package models

import javax.inject.Inject
import javax.inject.Singleton

import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import play.api.Configuration
import play.api.Logger

import scala.collection.JavaConversions._

@Singleton
class BookingsReminderPage @Inject() (val driver: WebDriver, val conf: BookingsReminderPageConf) extends Page {
  val logger: Logger = Logger(this.getClass)
  
  def goToCourtsTable = {
    logger.trace(s"goToCourtsTable()")
    val buttons = driver.findElements(By.cssSelector("#workpage button")).toList
    buttons(1).click
  }
  
  def isCurrentPage = {
    logger.trace(s"isCurrentPage()")
    driver.findElements(By.id("liste_reservation")).nonEmpty
  }
}

@Singleton
class BookingsReminderPageConf @Inject() (conf: Configuration) {
  
}
