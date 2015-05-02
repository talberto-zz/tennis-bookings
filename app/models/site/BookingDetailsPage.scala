package models.site

import javax.inject.{Inject, Singleton}

import org.openqa.selenium.{By, WebDriver, WebElement}
import play.api.{Configuration, Logger}

import scala.collection.JavaConversions._

@Singleton
class BookingDetailsPage @Inject() (val driver: WebDriver, val screenshotsHelper: ScreenshotsHelper) extends Page {

  val logger = Logger(getClass)
  /**
   * Choose the default partner (Benedicte)
   */
  def choosePartner = {
    logger.trace(s"choosePartner()")
    screenshotsHelper.takeScreenshot()
    // Open the select first
    val select = driver.findElement(By.id("CHAMP_TYPE_1-button"))
    select.click
    screenshotsHelper.takeScreenshot()

    // Find the partner and click it too
    val partners: List[WebElement] = driver.findElements(By.cssSelector("#CHAMP_TYPE_1-menu li")).toList
    partners(4).click
    screenshotsHelper.takeScreenshot()
  }
  
  def confirmBooking = {
    logger.trace(s"confirmBooking()")
    screenshotsHelper.takeScreenshot()
    val buttons = driver.findElements(By.cssSelector("#workpage button"))
    buttons(1).click
    screenshotsHelper.takeScreenshot()
  }
  
  def isCurrentPage = driver.findElements(By.id("CHAMP_TYPE_1-button")).nonEmpty
}
