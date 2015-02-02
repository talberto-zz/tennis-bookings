package models

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

import play.api.Configuration
import play.api.test.FakeApplication

@RunWith(classOf[JUnitRunner])
class CourtsTablePageSpec extends Specification {
  
  "The 'CourtsTablePage'" should {
    "open the 'ChoosePartnerPage' when clicked twice over a court" in new CourtsTablePageSpecBeforeAfter {
      courtsTablePage.book(Courts.COURT_15, Hours.HOUR_17)
      
      courtsTablePage.driver.getCurrentUrl must equalTo(destUrl)
    }
  }
}

trait CourtsTablePageSpecBeforeAfter extends After {
  // Setup the LoginPage 
  val app = FakeApplication()
  val conf: Configuration = app.configuration
  val url = conf.getString("courtsTablePage.url").get
  val destUrl = conf.getString("choosePartnerPage.url").get
  val driver: WebDriver = new ChromeDriver
  
  // Navigate to the page
  driver.get(url)
  val courtsTablePage = CourtsTablePage(driver, CourtsTablePageConf(conf))
  
  def after = {
    courtsTablePage.driver.close
  }
}