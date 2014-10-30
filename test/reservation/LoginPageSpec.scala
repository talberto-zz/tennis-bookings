package reservation

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import org.openqa.selenium.firefox.FirefoxDriver

@RunWith(classOf[JUnitRunner])
class LoginPageSpec extends Specification {
  val driver = new FirefoxDriver()
  val user = sys.env("user")
  val pass = sys.env("password")
  
  "LoginPage" should {
    "should login" in {
      val loginPage = LoginPage(driver)
      
      loginPage.login(user, pass)
      
      success
    }
  }
}