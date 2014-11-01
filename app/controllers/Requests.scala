package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.db.Dao
import models.db.Request
import org.joda.time.DateTime

object Requests extends Controller {
  implicit val requestFormat = Json.format[Request]
  
  def index = Action { implicit request =>
    val allRequests: Seq[Request] = Seq(Request(123, new DateTime()))
    
    render {
      case Accepts.Html() => Ok(views.html.request.index(allRequests))
      case Accepts.Json() => Ok(Json.toJson(allRequests))
    }
  }
}