package models.db

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Scheduler}
import models.AppConfiguration._
import models.site.{BookingsLimitReachedException, AlreadyBookedException, TennisSite}
import org.joda.time.{DateTime, LocalTime}
import play.api.Logger

import scala.concurrent.duration._

@Singleton
class BookingsServices @Inject() (val bookingsRepository: BookingsRepository, val commentsServices: CommentsServices, val tennisSite: TennisSite) {
  
  // Constants  
  val logger = Logger(getClass)
  val actorSystem = ActorSystem()
  val scheduler: Scheduler = actorSystem.scheduler
  implicit val executor = actorSystem.dispatcher
  
  /**
   * Transforms a org.joda.time.Duration into a scala.concurrent.duration.FiniteDuration
   */
  implicit def jodaDurationToDuration(duration: org.joda.time.Duration): FiniteDuration = Duration(duration.getMillis, MILLISECONDS)
  
  def book(booking: Booking): Unit = {
    logger.trace(s"book($booking)")
    val newBooking = bookingsRepository.save(booking)
    commentsServices.addCommentToBooking(newBooking.id, s"Created")
    scheduleBooking(newBooking)
  }
  
  def cancelBooking(id: Long) = {
    logger.trace(s"cancelBooking($id)")
    bookingsRepository.delete(id)
  }
  
  def list: Seq[Booking] = {
    logger.trace(s"list()")
    bookingsRepository.list
  }
  
  def find(id: Long) = {
    logger.trace(s"find($id)")
    bookingsRepository.find(id)   
  }
  
  protected def canBookToday(booking: Booking) = tennisSite.canBookToday(booking)
  
  protected def canBookNow(booking: Booking) = canBookToday(booking) && LocalTime.now.isAfter(TennisSite.BookingStartingHour)
  
  protected def tryToBook(booking: Booking) = {
    logger.trace(s"tryToBook($booking)")
    try {
      commentsServices.addCommentToBooking(booking.id, "Trying to book")
      tennisSite.book(booking)
      bookingsRepository.update(booking.copy(status = Booking.Status.SUCCESSFULLY_BOOKED))
      commentsServices.addCommentToBooking(booking.id, "Succesfully booked")
    } catch {
      case e: AlreadyBookedException => {
        logger.error(s"Error trying to book [$booking]", e)
        bookingsRepository.update(booking.copy(status = Booking.Status.ALREADY_BOOKED))
        commentsServices.addCommentToBooking(booking.id, "Couldn't book, the booking seems to be already booked")
      }
      
      case e: BookingsLimitReachedException => {
        logger.error(s"Error trying to book [$booking]", e)
        bookingsRepository.update(booking.copy(status = Booking.Status.BOOKINGS_LIMIT_REACHED))
        commentsServices.addCommentToBooking(booking.id, "Couldn't book, looks like you've reached your limit of bookings")
      }
      
      case e: Throwable => {
        logger.error(s"Unexpected error trying to book [$booking]", e) 
        bookingsRepository.update(booking.copy(status = Booking.Status.FAILED))
        commentsServices.addCommentToBooking(booking.id, "An unexpected error happened trying to book")
      }
    }
  }
  
  protected def scheduleBooking(booking: Booking) = {
    logger.trace(s"scheduleBooking($booking)")
    val when = whenToTryToBook(booking)
    val duration = new org.joda.time.Duration(DateTime.now, when)
    val task = scheduler.scheduleOnce(
      delay = duration,
      runnable = new Runnable {
        def run = {
          tryToBook(booking)
        }
      })
    bookingsRepository.update(booking.copy(status = Booking.Status.SCHEDULED))
    commentsServices.addCommentToBooking(booking.id, s"The booking has been scheduled for execution on ${DateTimeFormatter.print(when)}")
  }
  
  protected def whenToTryToBook(booking: Booking) = {
    logger.trace(s"whenToTryToBook($booking)")
    if(canBookNow(booking)) {
      DateTime.now.plusSeconds(10)
    } else {
      booking.date.minusDays(TennisSite.DaysOfDifference.getDays()).toDateTime(TennisSite.BookingStartingHour, ParisTimeZone) 
    }
  }
}

