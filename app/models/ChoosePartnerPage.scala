package models

import scala.collection.JavaConversions._

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class ChoosePartnerPage private(val driver: WebDriver, val url: String) {

  /**
   * Choose the default partner (Benedicte)
   */
  def choose = {
    // Open the select first
    val select = driver.findElement(By.id("CHAMP_TYPE_1-button"))
    select.click
    
    // Find the partner and click it too
    val partners: List[WebElement] = driver.findElements(By.cssSelector("#CHAMP_TYPE_1-menu li")).toList
    partners(4).click
  }
}

object ChoosePartnerPage {
  def apply(driver: WebDriver, url: String) = new ChoosePartnerPage(driver, url)
}