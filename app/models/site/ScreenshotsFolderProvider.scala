package models.site

import java.io.File
import javax.inject.{Inject, Named, Singleton}

import com.google.common.base.Preconditions._
import com.google.inject.Provider

/**
 * Created by trodriguez on 01/05/15.
 */
@Singleton
class ScreenshotsFolderProvider @Inject() (@Named("webdriver.screenshots.folder") val screenshotsFolderPath: String) extends Provider[File] {

  override def get(): File = {
    val screenshotsFolder = new File(screenshotsFolderPath)
    checkArgument(screenshotsFolder.exists() && screenshotsFolder.isDirectory && screenshotsFolder.canWrite, "The screenshotsFolder specified [%s] specified is not valid", screenshotsFolderPath)
    screenshotsFolder
  }
}
