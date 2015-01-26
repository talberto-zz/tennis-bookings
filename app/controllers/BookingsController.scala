package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import org.joda.time.DateTime
import models.Booking
import models.BookingsRepository
import views.html._
import controllers.EnumUtils._

/**
 * Controller for Booking's
 */
object BookingsController extends Controller {

  val logger: Logger = Logger(this.getClass)
  
  val bookingForm = Form(
    mapping(
      "id" -> ignored(None.asInstanceOf[Option[Long]]), // Set the id always to None
      "dateTime" -> jodaDate("yyyy-MM-dd HH:mm"),
      "status" -> ignored(Booking.Status.PENDING) // Set the status always to PENDING
    )(Booking.apply)(Booking.unapply)
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
        logger.debug("Form contains errors, sending redirection")
        BadRequest(views.html.bookings.newForm(formWithErrors))
      },
      booking => {
        logger.debug("Form doesn't contains errors, creating new booking")
        BookingsRepository.create(booking.dateTime)
        Redirect(routes.BookingsController.index())
      }
    )
  }

//  def newForm = Action { implicit req =>
//    views.html.bookings.newForm
//  }
//  
//  def edit = Action { implicit req =>
//    val booking = BookingRepository.findById(id)
//    
//    views.html.bookings.edit(booking)
//  }
//  
//  def create = Action { implicit req =>
//    views.html.bookings.newForm
//  }
//  
//  def update = Action { implicit req =>
//    views.html.bookings.newForm
//  }
//  
//  def destroy = Action { implicit req =>
//    views.html.bookings.newForm
//  }

//  def index = Action { implicit req =>
//    val allRequests: Seq[Booking] = BookingRepository.findAll
//    
//    render {
//      case Accepts.Html() => Ok(Booking.index(allRequests))
//      case Accepts.Json() => Ok(Json.toJson(allRequests))
//    }
//  }
//  
//  def newForm = Action {
//    Ok(views.html.Booking.newForm(requestForm))
//  }
//  
//  def create = Action { implicit request: Request[AnyContent] =>
//    render {
//      case Accepts.Html() => createAny
//      case Accepts.Json() => createJson
//    }
//  }
//  
//  def createAny(implicit request: Request[AnyContent]) = { 
//    requestForm.bindFromRequest.fold(
//      formWithErrors => {
//        BadRequest(views.html.Booking.newForm(formWithErrors))
//      },
//      Booking => {
//        BookingRepository.insert(Booking)
//        Redirect(routes.Bookings.index())
//      }
//    )
//  }
//  
//  def createJson(implicit request: Request[AnyContent]) = { 
//    val jsonBody = request.body.asJson
//    jsonBody match {
//      case Some(jsValue) => {
//        val resReq = jsValue.validate[Booking]
//        resReq.fold(
//          errors => {
//            BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
//          },
//          resReq => { 
//            BookingRepository.insert(resReq)
//            Ok(Json.obj("status" ->"OK", "message" -> (s"Reservation request saved")))
//          })
//      }
//      case None => BadRequest(Json.obj("status" -> "KO", "message" -> "Json not valid"))
//    }
//  }
//  
//  def show(id: String) = Action { implicit req =>
//    try {
//      val LongId: Long = id.toLong
//      val resReq = BookingRepository.findById(LongId)
//    
//      resReq match {
//        case Some(resReq) => Ok(Json.toJson(resReq))
//        case None => BadRequest(Json.obj("status" -> "KO", "message" -> s"Couldn't find a request with id ${id}"))
//      }
//    } catch {
//      case e: NumberFormatException => BadRequest(Json.obj("status" -> "KO", "message" -> s"Cannot parse ${id} as a Longeger"))
//    }
//  }
}