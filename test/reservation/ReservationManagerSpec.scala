package reservation

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ReservationManagerSpec extends Specification {
  val url = "http://adsl.icerium.net/_start/index.php?club=32920202&idact=101";
  
  "ReservationManager" should {
    "return all the reservations of a given day" in {
      val reservations = ReservationManager.all
      
      pending
    }
  }
}