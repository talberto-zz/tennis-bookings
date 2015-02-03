package models

import akka.actor.ActorSystem
import akka.actor.Scheduler

import java.util.concurrent.TimeUnit

import org.joda.time.LocalDate

import play.api.Logger
import play.api.Play

import scala.concurrent.duration._

class BookingsManager(val tennisSite: TennisSite, val bookingsRepository: BookingsRepository, val bookingsScheduler: BookingsScheduler) {
  
  val logger = Logger(getClass)
  
  def book(booking: Booking): Unit = {
    logger.trace(s"booking($booking)")
    if(canBookToday(booking)) {
      tryToBook(booking)
    } else {
      scheduleBooking(booking)
    }
  }
  
  def cancelBooking(id: Long) = {
    logger.trace(s"cancelBooking($id)")
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
    logger.debug(s"Trying to book today ($booking)")
    tennisSite.book(booking)
  }
  
  protected def scheduleBooking(booking: Booking) = {
    logger.debug(s"Will attempt to schedule later ($booking)")
    bookingsScheduler.scheduleBooking(this, booking)
  }
}

object BookingsManager {
  def apply() = {
    val tennisSite = TennisSite(Play.current.configuration)
    val bookingsRepository = BookingsRepository()
    val bookingsScheduler = BookingsScheduler()
    new BookingsManager(tennisSite, bookingsRepository, bookingsScheduler)
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
