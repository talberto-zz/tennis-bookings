package models.site

import javax.inject.{Inject, Singleton}

import org.openqa.selenium._
import play.api.{Configuration, Logger}

import scala.collection.JavaConversions._

@Singleton
class LoginPage @Inject() (val driver: WebDriver, val conf: LoginPageConf, val screenshotsHelper: ScreenshotsHelper) extends Page {
  
  val logger = Logger(getClass)
  
  // Gets the page
  def get = {
    logger.trace(s"get()")
    driver.get(conf.url)
  }
  
  def doLogin = {
    logger.trace(s"doLogin()")
    screenshotsHelper.takeScreenshot()

    // Get username field
    val userid: WebElement = driver.findElement(By.cssSelector("""input[name="userid"]"""))
    userid.sendKeys(conf.username)
    screenshotsHelper.takeScreenshot()

    val userkey: WebElement = driver.findElement(By.cssSelector("""input[name="userkey"]"""))
    userkey.sendKeys(conf.password)
    screenshotsHelper.takeScreenshot()

    driver.findElement(By.id("btn_identification")).click()
    screenshotsHelper.takeScreenshot()
  }
  
  def isCurrentPage = driver.findElements(By.id("btn_identification")).nonEmpty
}

@Singleton
class LoginPageConf @Inject() (conf: Configuration) {
  val url: String = conf.getString("loginPage.url").get
  val username: String = conf.getString("loginPage.username").get
  val password: String = conf.getString("loginPage.password").get
}
