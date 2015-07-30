package models

import java.io.File
import javax.inject.{Inject, Singleton}

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import models.site._
import net.codingwell.scalaguice.ScalaModule
import org.openqa.selenium.WebDriver
import play.api.{Configuration, Environment}

class BookingsModule @Inject() (val environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

  val tmpDir = sys.props("java.io.tmpdir")

  def configure {
    val screenshotsFolder = configuration.getString("webdriver.screenshots.folder").getOrElse(tmpDir)

    // Use always the remote web driver
    bind[WebDriverFactory].to[RemoteWebDriverFactory]
    bindConstant().annotatedWith(Names.named("webdriver.screenshots.folder")).to(screenshotsFolder)
    bind[File].annotatedWith(classOf[ScreenshotsFolder]).toProvider(classOf[ScreenshotsFolderProvider])
    bind[WebDriver].toProvider[WebDriverProvider].in[Singleton]
  }
}
