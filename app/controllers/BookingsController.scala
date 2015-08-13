package controllers

import javax.inject.{Inject, Singleton}

import controllers.Forms._
import models.AppConfiguration._
import models.db.{Booking, BookingsServices, CommentsServices}
import org.joda.time.DateTime
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc._

/**
 * Controller for Booking's
 */
@Singleton
class BookingsController @Inject() (val bookingsManager: BookingsServices, val commentsServices: CommentsServices, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val logger: Logger = Logger(this.getClass)
  
  val bookingForm: Form[Booking] = Form(
    mapping(
      "id" -> ignored(null.asInstanceOf[Long]), // Set the id always null Long
      "creationDate" -> ignored(DateTime.now(ParisTimeZone)),
      "lastModified" -> ignored(DateTime.now(ParisTimeZone)),
      "date" -> jodaLocalDate,
      "time" -> jodaLocalTime(HourPattern, ParisTimeZone),
      "court" -> number,
      "status" -> ignored(Booking.Status.SUBMITTED) // Set the status always to NEW
    )(Booking.fromDateAndTime)(Booking.unapplyDateAndTime)
  )
  
  val idForm = Form(
    single(
      "id" -> longNumber    
    )    
  )

  implicit val implicitBarWrites = new Writes[Booking] {
    def writes(booking: Booking): JsValue = {
      Json.obj(
        "id" -> booking.id,
        "creationDate" -> booking.creationDate,
        "lastModified" -> booking.lastModified,
        "dateTime" -> booking.dateTime,
        "status" -> booking.status,
        "court" -> booking.court
      )
    }
  }

  def list = Action {
    Ok(Json.toJson(bookingsManager.list))
  }

  def create = Action { implicit req =>
    logger.debug("Received booking creation request")
    bookingForm.bindFromRequest.fold(
      formWithErrors => {
        logger.debug("Form contains errors, sending bad request")
        BadRequest("Bad Request")
      },
      booking => {
        logger.debug("Form doesn't contains errors, creating new booking")
        bookingsManager.book(booking)
        // FIXME should immediately return the new booking
        Ok(Json.toJson(Json.obj()))
      }
    )
  }
  
  def delete(id: Long) = Action { implicit request =>
    logger.debug(s"Received booking deletion request for id [$id]")
    bookingsManager.cancelBooking(id)
    Ok(Json.toJson(Json.obj()))
  }

  // FIXME implement update booking action
  def update(id: Long) = TODO
}