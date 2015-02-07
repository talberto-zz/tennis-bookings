package models

import akka.actor.ActorSystem
import akka.actor.Scheduler
import akka.actor.Cancellable

import java.util.concurrent.TimeUnit

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.DateTime

import play.api.Logger
import play.api.Play

import scala.concurrent.duration._

class BookingsManager(val bookingsRepository: BookingsRepository, val bookingsScheduler: BookingsScheduler, val tennisSite: TennisSite) {
  
  // Constants  
  val logger = Logger(getClass)
  val actorSystem = ActorSystem()
  val scheduler: Scheduler = actorSystem.scheduler
  implicit val executor = actorSystem.dispatcher
  val scheduledTasks: scala.collection.mutable.Map[Long, Cancellable] = scala.collection.concurrent.TrieMap()
  
  /**
   * Transforms a org.joda.time.Duration into a scala.concurrent.duration.FiniteDuration
   */
  implicit def jodaDurationToDuration(duration: org.joda.time.Duration): FiniteDuration = Duration(duration.getMillis, MILLISECONDS)
  
  def book(booking: Booking): Unit = {
    logger.trace(s"book($booking)")
    bookingsRepository.save(booking)
    scheduleBooking(booking)
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
  
  protected def canBookToday(booking: Booking) = LocalDate.now.plusDays(3).isAfter(booking.date)
  
  protected def tryToBook(booking: Booking) = {
    logger.trace(s"tryToBook($booking)")
    logger.debug(s"Trying to book today [$booking]")
    tennisSite.book(booking)
  }
  
  protected def scheduleBooking(booking: Booking) = {
    logger.trace(s"scheduleBooking($booking)")
    if(canBookToday(booking)) {
      val task = scheduler.scheduleOnce(
        delay = Duration(5, SECONDS),
        runnable = new Runnable {
          def run = {
            tryToBook(booking)
          }
        })
      scheduledTasks += booking.id -> task
    } else {
      logger.debug(s"Will attempt to schedule later [$booking]")
      val today = LocalDate.now()
      val whenToTryToBook = today.plusDays(TennisSite.DaysOfDifference.getDays()).toDateTime(TennisSite.BookingStartingHour.minusMinutes(5))
      val duration = new org.joda.time.Duration(DateTime.now, whenToTryToBook)
      val task = scheduler.schedule(
        initialDelay = duration,
        interval = Duration(30, SECONDS),
        runnable = new Runnable {
          def run = {
            tryToBook(booking)
          }
        })
      scheduledTasks += booking.id -> task
    }
  }
}

object BookingsManager {
  def apply() = {
    val tennisSite = TennisSite(Play.current.configuration)
    val bookingsRepository = BookingsRepository()
    val bookingsScheduler = BookingsScheduler()
    new BookingsManager(bookingsRepository, bookingsScheduler, tennisSite)
  }
  
  def apply(bookingsRepository: BookingsRepository, bookingsScheduler: BookingsScheduler, tennisSite: TennisSite) = {
    new BookingsManager(bookingsRepository, bookingsScheduler, tennisSite)
  }
}

class BookingsScheduler {
  val logger = Logger(getClass)
  val actorSystem = ActorSystem()
  val scheduler: Scheduler = actorSystem.scheduler
  implicit val executor = actorSystem.dispatcher
  
  def scheduleBooking(bookingsManager: BookingsManager, booking: Booking) = {
    logger.debug(s"Will attempt to schedule later ($booking)")
    def task = new Runnable {
      def run = {
        bookingsManager.book(booking)
      }
    }
    scheduler.schedule(
        initialDelay = Duration(5, MINUTES),
        interval = Duration(5, MINUTES),
        runnable = task)
  }
}

object BookingsScheduler {
  def apply() = new BookingsScheduler
}
