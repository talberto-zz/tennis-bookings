package models

import org.openqa.selenium._

import javax.inject.Inject
import javax.inject.Singleton

import play.api.Logger
import play.api.Configuration

import scala.collection.JavaConversions._

@Singleton
class LoginPage @Inject() (val driver: WebDriver, val conf: LoginPageConf) extends Page {
  
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
  
  def isCurrentPage = driver.findElements(By.id("btn_identification")).nonEmpty
}

@Singleton
class LoginPageConf @Inject() (conf: Configuration) {
  val url: String = conf.getString("loginPage.url").get
  val username: String = conf.getString("loginPage.username").get
  val password: String = conf.getString("loginPage.password").get
}
