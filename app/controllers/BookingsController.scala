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
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc._

import scala.concurrent.Future

/**
 * Controller for Booking's
 */
@Singleton
class BookingsController @Inject() (val bookingsService: BookingsServices, val commentsServices: CommentsServices, val messagesApi: MessagesApi, val configuration: Configuration) extends Controller with I18nSupport {

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

  def list = Action.async {
    bookingsService.list.map(bookings => Ok(Json.toJson(bookings)))
  }

  def create = Action.async { implicit req =>
    logger.debug("Received booking creation request")
    bookingForm.bindFromRequest.fold(
      formWithErrors => {
        logger.debug("Form contains errors, sending bad request")
        Future(BadRequest("Bad Request"))
      },
      booking => {
        logger.debug("Form doesn't contains errors, creating new booking")
        bookingsService.book(booking)
        // FIXME should immediately return the new booking
        Future(Ok(Json.toJson(Json.obj())))
      }
    )
  }
  
  def delete(id: Long) = Action.async { implicit request =>
    logger.debug(s"Received booking deletion request for id [$id]")
    val eventualCancel = bookingsService.cancelBooking(id)
    eventualCancel.map(_ => Ok(Json.toJson(Json.obj())))
  }

  // FIXME implement update booking action
  def update(id: Long) = TODO
}