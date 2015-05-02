package controllers

import javax.inject.{Inject, Singleton}

import controllers.Forms._
import models.AppConfiguration._
import models.db.{Booking, BookingsServices, CommentsServices}
import org.joda.time.DateTime
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

/**
 * Controller for Booking's
 */
@Singleton
class BookingsController @Inject() (val bookingsManager: BookingsServices, val commentsServices: CommentsServices) extends Controller {

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
  /* ACTIONS */
  def index = Action { implicit req =>
    val bookings = bookingsManager.list
    
    Ok(views.html.bookings.index(bookings))
  }
  
  def newForm = Action { implicit req =>
    Ok(views.html.bookings.newForm(bookingForm))
  }
  
  def create = Action { implicit req =>
    logger.debug("Received booking creation request")
    bookingForm.bindFromRequest.fold(
      formWithErrors => {
        logger.debug("Form contains errors, sending bad request")
        BadRequest(views.html.bookings.newForm(formWithErrors))
      },
      booking => {
        logger.debug("Form doesn't contains errors, creating new booking")
        bookingsManager.book(booking)
        Redirect(routes.BookingsController.index())
      }
    )
  }
  
  def delete(id: Long) = Action { implicit request =>
    logger.debug(s"Received booking deletion request for id [$id]")
    bookingsManager.cancelBooking(id)
    Redirect(routes.BookingsController.index())
  }
  
  def show(id: Long) = Action {
    val booking = bookingsManager.find(id).get
    val comments = commentsServices.findByBookingId(booking.id)
    
    Ok(views.html.bookings.show(booking, comments))
  }
}