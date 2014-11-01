package controllers

import play.api._
import play.api.mvc._

object Requests extends Controller {
  def index = Action {
    Ok(views.html.request.index())
  }
}