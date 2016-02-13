package models.site

import javax.inject.{Inject, Singleton}

import org.openqa.selenium.{By, WebDriver}
import play.api.{Configuration, Logger}

import scala.collection.JavaConversions._

@Singleton
class BookingsReminderPage @Inject() (val driver: WebDriver, val screenshotsHelper: ScreenshotsHelper) extends Page {
  val logger: Logger = Logger(this.getClass)
  
  def goToCourtsTable = {
    logger.trace(s"goToCourtsTable()")
    screenshotsHelper.takeScreenshot()

    val buttons = driver.findElements(By.cssSelector("#workpage button")).toList
    buttons(1).click
    screenshotsHelper.takeScreenshot()
  }
  
  def isCurrentPage = {
    logger.trace(s"isCurrentPage()")
    driver.findElements(By.id("liste_reservation")).nonEmpty
  }
}
