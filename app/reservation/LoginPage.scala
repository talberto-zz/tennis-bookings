package reservation

import org.openqa.selenium._

class LoginPage(val driver: WebDriver) {

  private[this] val url = "http://adsl.icerium.net/_start/index.php?club=32920202&idact=101"
  private[this] val user = sys.env("user")
  private[this] val pass = sys.env("password")
  
  def login(user: String, pass: String) {
    driver.get(url)
    
    val userInput = driver.findElement(By.cssSelector("input[name='userid']"))
    val passInput = driver.findElement(By.cssSelector("input[name='userkey']"))
    val submitButton = driver.findElement(By.id("btn_identification"))
    
    userInput.sendKeys(user)
    passInput.sendKeys(pass)
    submitButton.click()
  }
}

object LoginPage {
  def apply(driver: WebDriver) = new LoginPage(driver)
}