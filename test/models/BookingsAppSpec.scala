package models

import com.google.inject.Guice

import org.specs2.mutable._
import org.specs2.runner._

import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class BookingsAppSpec extends Specification {

  val injector = Guice.createInjector(new TestBookingsModule()) 
  
  "BookingsApp" should {
    "return an instance of BookingsManager" in {
      val bookingsManager = injector.getInstance(classOf[BookingsManager])
      bookingsManager must not beNull
    }
  }
}
