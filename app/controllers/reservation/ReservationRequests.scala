package controllers.reservation

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.DateTime
import models.db.ReservationRequest
import models.db.Implicits._
import models.db.State
import models.db.State._
import views.html._
import controllers.EnumUtils._

object ReservationRequests extends Controller {
  implicit val stateFormat = enumFormat(State)
  implicit val requestFormat = Json.format[ReservationRequest]
  
  val requestForm = Form(
    mapping(
      "id" -> number,
      "date" -> longNumber,
      "state" -> number
    )(ReservationRequest.apply)(ReservationRequest.unapply _)
  )
  
  def index = Action { implicit req =>
    val allRequests: Seq[ReservationRequest] = Seq(ReservationRequest(123, new DateTime(), PENDING))
    
    render {
      case Accepts.Html() => Ok(reservationRequest.index(allRequests))
      case Accepts.Json() => Ok(Json.toJson(allRequests))
    }
  }
  
  def newForm = {
    
  }
  
  def create(reservationRequest: ReservationRequest) = {
    
  }
  
  def show(id: String) = Action { implicit req =>
    val request: ReservationRequest = ReservationRequest(234, new DateTime(), PENDING)
    
    render {
      case Accepts.Html() => Ok(reservationRequest.view(request))
      case Accepts.Json() => Ok(Json.toJson(request))
    }
  }
}