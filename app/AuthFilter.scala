import play.api.Logger
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class AuthFilter extends Filter {
  def decodeBasicAuth(auth: String) = {
    val baStr = auth.replaceFirst("Basic ", "")
    val Array(user, pass) = new String(new sun.misc.BASE64Decoder().decodeBuffer(baStr), "UTF-8").split(":")
    (user, pass)
  }
  
  def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    requestHeader.headers.get("Authorization").map{ basicAuth =>
      val (user, pass) = decodeBasicAuth(basicAuth)
      
      (user, pass) match {
        case ("tennis", "tennis") => nextFilter(requestHeader)
        case _ => Future(Unauthorized)
      }
    }.getOrElse(Future(Unauthorized))
  }
}
