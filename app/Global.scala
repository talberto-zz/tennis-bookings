import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import models.BookingsApp

object Global extends WithFilters(new AuthFilter()) {
  
  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
    Future {
      Redirect(controllers.routes.BookingsController.index())
    }
  }  
  
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    BookingsApp.injector.getInstance(controllerClass)
  }
}
