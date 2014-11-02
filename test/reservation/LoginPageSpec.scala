package reservation

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import org.openqa.selenium.chrome.ChromeDriver

@RunWith(classOf[JUnitRunner])
class LoginPageSpec extends Specification {
  val driver = new ChromeDriver()
  val user = sys.props("user")
  val pass = sys.props("password")
  
  "LoginPage" should {
    "should login" in {
      val loginPage = LoginPage(driver)
      
      loginPage.login(user, pass)
      
      success
    }
  }
}