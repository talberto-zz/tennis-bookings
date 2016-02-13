package models.site

import java.io.File
import javax.inject.Inject

import com.google.common.io.Files
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.openqa.selenium.remote.Augmenter
import org.openqa.selenium.{OutputType, TakesScreenshot, WebDriver}
import play.api.Logger

/**
 * Created by trodriguez on 01/05/15.
 */
class ScreenshotsHelper @Inject() (val webdriver: WebDriver, @ScreenshotsFolder val screenshotsFolder: File) {

  val logger = Logger(getClass)
  val screenshotCapable: TakesScreenshot = new Augmenter().augment(webdriver).asInstanceOf[TakesScreenshot]
  val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");

  private[this] def generateScreenshotName: String = {
    s"${DateTime.now().toString(fmt)}.png"
  }

  def takeScreenshot(filename: String = generateScreenshotName): String = {
    val screenshotFile = new File(screenshotsFolder, filename)

    logger.debug(s"Taking new screenshot [${screenshotFile}]")
    val bytes = screenshotCapable.getScreenshotAs(OutputType.BYTES)
    Files.write(bytes, screenshotFile)
    filename
  }
}
