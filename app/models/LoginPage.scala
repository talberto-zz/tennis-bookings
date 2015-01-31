package models

import org.openqa.selenium._

class LoginPage private(val driver: WebDriver, val url: String, val username: String, val password: String) {
  
  // Gets the page
  def get = {
    driver.get(url)
  }
  
  def doLogin = {
    // Get username field
    val userid: WebElement = driver.findElement(By.cssSelector("""input[name="userid"]"""))
    userid.sendKeys(username)
    val userkey: WebElement = driver.findElement(By.cssSelector("""input[name="userkey"]"""))
    userkey.sendKeys(password)
    driver.findElement(By.id("btn_identification")).click()
  }
}

object LoginPage {
  def apply(driver: WebDriver, url: String, username: String, password: String) = new LoginPage(driver, url, username, password)
}