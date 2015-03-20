package models

import javax.inject.Inject
import javax.inject.Singleton

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import play.api.Logger
import play.api.Configuration

import scala.collection.JavaConversions._

@Singleton
class BookingDetailsPage @Inject() (val driver: WebDriver, val conf: BookingDetailsPageConf) extends Page {

  val logger = Logger(getClass)
  /**
   * Choose the default partner (Benedicte)
   */
  def choosePartner = {
    logger.trace(s"choosePartner()")
    // Open the select first
    val select = driver.findElement(By.id("CHAMP_TYPE_1-button"))
    select.click
    
    // Find the partner and click it too
    val partners: List[WebElement] = driver.findElements(By.cssSelector("#CHAMP_TYPE_1-menu li")).toList
    partners(4).click
  }
  
  def confirmBooking = {
    logger.trace(s"confirmBooking()")
    val buttons = driver.findElements(By.cssSelector("#workpage button"))
    buttons(1).click
  }
  
  def isCurrentPage = driver.findElements(By.id("CHAMP_TYPE_1-button")).nonEmpty
}

@Singleton
class BookingDetailsPageConf @Inject() (conf: Configuration) {
  
}
