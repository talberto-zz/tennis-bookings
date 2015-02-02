package models

import org.openqa.selenium._

import play.api.Logger
import play.api.Configuration

class LoginPage private(val driver: WebDriver, val conf: LoginPageConf) {
  
  val logger = Logger(getClass)
  
  // Gets the page
  def get = {
    logger.trace(s"get()")
    driver.get(conf.url)
  }
  
  def doLogin = {
    logger.trace(s"doLogin()")
    // Get username field
    val userid: WebElement = driver.findElement(By.cssSelector("""input[name="userid"]"""))
    userid.sendKeys(conf.username)
    val userkey: WebElement = driver.findElement(By.cssSelector("""input[name="userkey"]"""))
    userkey.sendKeys(conf.password)
    driver.findElement(By.id("btn_identification")).click()
  }
}

object LoginPage {
  def apply(driver: WebDriver, conf: LoginPageConf) = new LoginPage(driver, conf)
}

case class LoginPageConf(val url: String, val username: String, val password: String)

object LoginPageConf {
  def apply(conf: Configuration): LoginPageConf = LoginPageConf(
        conf.getString("loginPage.url").get,
        conf.getString("loginPage.username").get,
        conf.getString("loginPage.password").get)
}
