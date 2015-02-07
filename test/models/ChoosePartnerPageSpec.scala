package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class ChoosePartnerPageSpec extends Specification {

  "The 'ChoosePartnerPage'" should {
    "return to the 'CourtsTablePage' when the partner has been chosen" in new ChoosePartnerPageSpecBeforeAfter {
      choosePartnerPage.choose
      
      choosePartnerPage.driver.getCurrentUrl must equalTo(destUrl)
    }
    
    "recognize it's page" in new ChoosePartnerPageSpecBeforeAfter {
      ChoosePartnerPage.isCurrentPage(driver) must equalTo(true)
      driver.get("http://www.google.com")
      ChoosePartnerPage.isCurrentPage(driver) must equalTo(false)
    }
  }
}

trait ChoosePartnerPageSpecBeforeAfter extends After {
  // Setup the LoginPage 
  val app = FakeApplication()
  val conf: Configuration = app.configuration
  val url = conf.getString("choosePartnerPage.url").get
  val destUrl = conf.getString("courtsTablePage.url").get
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  driver.get(url)
  val choosePartnerPage = ChoosePartnerPage(driver, ChoosePartnerPageConf(conf))
  
  def after = {
    choosePartnerPage.driver.close
  }
}