package controllers

import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future

object ReservationsController extends Controller {

  val logger: Logger = Logger(this.getClass)

  def create = Action.async { implicit req =>
    logger.debug("Received booking creation request")
    Future(NotFound)
  }
}