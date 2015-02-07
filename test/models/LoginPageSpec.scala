package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class LoginPageSpec extends Specification {

  "The 'LoginPage' page" should {
    "get the LoginPage when get is called" in new LoginPageSpecBeforeAfter {
      loginPage.driver.getCurrentUrl must equalTo(url)
    }
    
    "get the CourtsTable when doLogin is called" in new LoginPageSpecBeforeAfter {  
      val destUrl = conf.getString("courtsTablePage.url").get
      
      loginPage.doLogin
      
      loginPage.driver.getCurrentUrl must equalTo(destUrl)
    }
    
    "recognize it's page" in new LoginPageSpecBeforeAfter {
      LoginPage.isCurrentPage(driver) must equalTo(true)
      driver.get("http://www.google.com")
      LoginPage.isCurrentPage(driver) must equalTo(false)
    }
  }
}

trait LoginPageSpecBeforeAfter extends BeforeAfter {
  // Setup the LoginPage 
  val app = FakeApplication()
  val conf: Configuration = app.configuration
  val url = conf.getString("loginPage.url").get
  val username = conf.getString("loginPage.username").get
  val password = conf.getString("loginPage.password").get
  val driver: WebDriver = new ChromeDriver
  val loginPage = LoginPage(driver, LoginPageConf(conf))
  
  // Get the page
  driver.get(url)
  def before = {
    
  }
  
  def after = {
    loginPage.driver.close
  }
}