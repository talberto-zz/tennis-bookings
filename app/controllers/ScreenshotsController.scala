package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import models.site.ScreenshotsFolder
import play.api.Logger
import play.api.mvc.{Action, Controller}
/**
 * A controller that serves screenshot files from a given location
 *
 * Created by trodriguez on 02/05/15.
 */
@Singleton
class ScreenshotsController @Inject() (@ScreenshotsFolder val screenshotsFolder: File) extends Controller {

  val logger = Logger(getClass)

  def image(fileName: String) = Action {
    logger.debug(s"Attemping to serve screenshot [${fileName}]")
    val file = new File(screenshotsFolder, fileName)

    if (file.exists()) {
      logger.debug(s"The file [${file}] exits, will serve it")
      Ok.sendFile(content = file, inline = true)
    }
    else {
      logger.debug(s"The file [${file}] doesn't exist, returning not found")
      NotFound
    }
  }
}
