package controllers.reservation

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.json.Writes._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import org.joda.time.DateTime
import models.db.ReservationRequest
import models.db.ReservationRequestRepository
import models.db.Implicits._
import models.db.Status
import views.html._
import controllers.EnumUtils._

object ReservationRequests extends Controller {
  implicit val statusFormat = enumFormat(models.db.Status)
//  implicit val requestFormat = Json.format[ReservationRequest]
  implicit val requestReads: Reads[ReservationRequest] = (
    (JsPath \ "id").read[Option[Int]] and
    (JsPath \ "date").read[DateTime] and
    (JsPath \ "status").read[models.db.Status.Status]
  ) (ReservationRequest.apply _)
  
  implicit val requestWrites: Writes[ReservationRequest] = (
    (JsPath \ "id").write[Option[Int]] and
    (JsPath \ "date").write[DateTime] and
    (JsPath \ "status").write[models.db.Status.Status]
  ) (unlift(ReservationRequest.unapply))
  
  val requestForm = Form(
    mapping(
      "id" -> Forms.optional(number),
      "date" -> longNumber,
      "status" -> number
    )(ReservationRequest.apply)(ReservationRequest.unapply)
  )
  
  def index = Action { implicit req =>
    val allRequests: Seq[ReservationRequest] = ReservationRequestRepository.findAll
    
    render {
      case Accepts.Html() => Ok(reservationRequest.index(allRequests))
      case Accepts.Json() => Ok(Json.toJson(allRequests))
    }
  }
  
  def newForm = Action {
    Ok(views.html.reservationRequest.newForm(requestForm))
  }
  
  def create = Action { implicit request: Request[AnyContent] =>
    render {
      case Accepts.Html() => createAny
      case Accepts.Json() => createJson
    }
  }
  
  def createAny(implicit request: Request[AnyContent]) = { 
    requestForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.reservationRequest.newForm(formWithErrors))
      },
      reservationRequest => {
        ReservationRequestRepository.insert(reservationRequest)
        Redirect(routes.ReservationRequests.index())
      }
    )
  }
  
  def createJson(implicit request: Request[AnyContent]) = { 
    val jsonBody = request.body.asJson
    jsonBody match {
      case Some(jsValue) => {
        val resReq = jsValue.validate[ReservationRequest]
        resReq.fold(
          errors => {
            BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
          },
          resReq => { 
            ReservationRequestRepository.insert(resReq)
            Ok(Json.obj("status" ->"OK", "message" -> (s"Reservation request saved")))
          })
      }
      case None => BadRequest(Json.obj("status" -> "KO", "message" -> "Json not valid"))
    }
  }
  
  def show(id: String) = Action { implicit req =>
    try {
      val intId: Int = id.toInt
      val resReq = ReservationRequestRepository.findById(intId)
    
      resReq match {
        case Some(resReq) => Ok(Json.toJson(resReq))
        case None => BadRequest(Json.obj("status" -> "KO", "message" -> s"Couldn't find a request with id ${id}"))
      }
    } catch {
      case e: NumberFormatException => BadRequest(Json.obj("status" -> "KO", "message" -> s"Cannot parse ${id} as a integer"))
    }
  }
}