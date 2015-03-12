package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class LoginPageSpec extends Specification {

  "The 'LoginPage' page" should {
    "get the LoginPage when get is called" in new LoginPageSpecBeforeAfter {
      loginPage.driver.getCurrentUrl must equalTo(url)
    }
    
    "get the CourtsTable when doLogin is called" in new LoginPageSpecBeforeAfter {  
      loginPage.doLogin
      
      CourtsTablePage.isCurrentPage(driver) must equalTo(true)
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
  val url = "http://localhost:8080/login/"
  val username = "TORODR"
  val password = "289G"
  val driver: WebDriver = new ChromeDriver
  val conf = LoginPageConf(url, username, password)
  val loginPage = LoginPage(driver, conf)
  
  // Get the page
  driver.get(url)
  def before = {
    
  }
  
  def after = {
    loginPage.driver.close
  }
}