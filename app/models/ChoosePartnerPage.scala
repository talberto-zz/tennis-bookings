package models

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import play.api.Logger
import play.api.Configuration

import scala.collection.JavaConversions._

class ChoosePartnerPage private(val driver: WebDriver, val conf: ChoosePartnerPageConf) {

  val logger = Logger(getClass)
  /**
   * Choose the default partner (Benedicte)
   */
  def choose = {
    logger.trace(s"choose()")
    // Open the select first
    val select = driver.findElement(By.id("CHAMP_TYPE_1-button"))
    select.click
    
    // Find the partner and click it too
    val partners: List[WebElement] = driver.findElements(By.cssSelector("#CHAMP_TYPE_1-menu li")).toList
    partners(4).click
  }
}

object ChoosePartnerPage {
  def apply(driver: WebDriver, conf: ChoosePartnerPageConf) = new ChoosePartnerPage(driver, conf)
}

case class ChoosePartnerPageConf(val url: String)

object ChoosePartnerPageConf {
  def apply(conf: Configuration): ChoosePartnerPageConf = ChoosePartnerPageConf(conf.getString("choosePartnerPage.url").get)
}