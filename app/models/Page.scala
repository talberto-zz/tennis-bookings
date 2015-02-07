package models

import org.openqa.selenium.WebDriver

trait PageObject {
  def isCurrentPage(driver: WebDriver): Boolean
}

trait Page {
  def isCurrentPage: Boolean
}