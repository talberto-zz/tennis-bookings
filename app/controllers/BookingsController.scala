package controllers

import models.Booking
import models.BookingsRepository
import org.joda.time.DateTime
import play.api._
import play.api.data._
import play.api.data.format.Formatter
import play.api.data.Forms._
import play.api.mvc._
import scala.util.control.Exception.catching
import views.html._



/**
 * Controller for Booking's
 */
object BookingsController extends Controller {

  val logger: Logger = Logger(this.getClass)
  
  /** Formatter status */
  implicit val statusFormatter = new Formatter[Booking.Status.Value] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Booking.Status.Value] = {
      try {
        val status: Option[Booking.Status.Value] = data.get(key).map(Booking.Status.withName(_))
        status.toRight(Seq(FormError(key, s"Couldn't find key $key")))
      } catch {
        case e: java.util.NoSuchElementException => Left(Seq(FormError(key, s"No status for input ${data(key)}")))
      }
    }
    
    def unbind(key: String, value: Booking.Status.Value): Map[String, String] = {
      Map(key -> value.toString)
    }
  }
  
  val bookingForm: Form[Booking] = Form(
    mapping(
      "id" -> ignored(null.asInstanceOf[Long]), // Set the id always null Long
      "dateTime" -> jodaDate("yyyy-MM-dd HH:mm"),
      "status" -> ignored(Booking.Status.PENDING) // Set the status always to PENDING
    )(Booking.apply)(Booking.unapply)
  )
  
  val idForm = Form(
    single(
      "id" -> longNumber    
    )    
  )
  /* ACTIONS */
  def index = Action { implicit req =>
    val bookings = BookingsRepository.findAll
    
    Ok(views.html.bookings.index(bookings))
  }

  def show(id: Long) = Action { implicit req =>
    val booking = BookingsRepository.findById(id)
    
    Ok(views.html.bookings.show(booking.get))
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
        BookingsRepository.create(booking.dateTime)
        Redirect(routes.BookingsController.index())
      }
    )
  }
  
  def edit(id: Long) = Action { implicit request =>
    logger.debug("Received edit request")
    val booking = BookingsRepository.findById(id).get
    val editForm = bookingForm.fill(booking)
    Ok(views.html.bookings.edit(editForm))
  }
  
  def update(id: Long) = Action { implicit request =>
    logger.debug("Received booking update request")
    bookingForm.bindFromRequest.fold(
      formWithErrors => {
        logger.debug("Form contains errors, sending bad request")
        BadRequest(views.html.bookings.edit(formWithErrors))
      },
      booking => {
        logger.debug("Form doesn't contains errors, updating booking")
        BookingsRepository.updateDateTime(id, booking.dateTime)
        Redirect(routes.BookingsController.index())
      }
    )
  }
  
  def delete(id: Long) = Action { implicit request =>
    logger.debug(s"Received booking deletion request for id [$id]")
    BookingsRepository.delete(id)
    Redirect(routes.BookingsController.index())
  }
}