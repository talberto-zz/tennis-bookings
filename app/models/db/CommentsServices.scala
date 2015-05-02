package models.db

import javax.inject.{Inject, Singleton}

import models.site.ScreenshotsHelper
import play.api.Logger

/**
 * Created by trodriguez on 02/05/15.
 */
@Singleton
class CommentsServices @Inject() (val commentsRepository: CommentsRepository, val screenshotsHelper: ScreenshotsHelper) {

  val logger = Logger(getClass)

  /**
   * Adds a comment to an already existing booking and optionally takes an screenshot
   *
   * @param bookingId
   * @param text
   * @param screenshot
   */
  def addCommentToBooking(bookingId: Long, text: String, screenshot: Boolean = true) = {
    logger.trace(s"addCommentToBooking(${bookingId}, ${text}, ${screenshot})")

    val screenshotName = screenshot match {
      case true => Option(screenshotsHelper.takeScreenshot())
      case false => None
    }

    commentsRepository.addCommentToBooking(bookingId, text, screenshotName)
  }

  def findByBookingId(bookingId: Long) = {
    commentsRepository.findByBookingId(bookingId)
  }
}
