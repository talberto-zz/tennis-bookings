package models.db

import models.site.TennisSite
import org.junit.runner.RunWith
import org.specs2._
import org.specs2.mock._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class BookingsManagerSpec extends Specification { def is = s2"""
  The 'BookingsManager'
    tries to book immediately if the booking date is within the next 3 days      ${c().e1}
    schedules the actual booking for later if the date is later than 3 days     ${c().e2}
  """

  case class c() extends Mockito {
    val tennisSite = mock[TennisSite]
    val bookingsRepository = mock[BookingsRepository]
    val commentsRepository = mock[CommentsRepository]
    val bookingsManager = new BookingsManager(bookingsRepository, commentsRepository, tennisSite)
    
    def e1 = {
      pending
    }
    
    def e2 = {
      pending
    }
  }
}
