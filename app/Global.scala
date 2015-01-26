import play.GlobalSettings
import play.api._
import play.libs.F._
import play.mvc._
import play.mvc.Http.RequestHeader
import play.mvc.Results._

object Global extends GlobalSettings {
  
  override def onHandlerNotFound(request: RequestHeader): Promise[Result] = {
    redirect(controllers.routes.BookingsController.index())
  }  
}