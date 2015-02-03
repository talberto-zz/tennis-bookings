package models

import akka.actor.ActorSystem
import akka.actor.Scheduler

import java.util.concurrent.TimeUnit

import org.joda.time.LocalDate

import play.api.Logger

import scala.concurrent.duration._

class BookingsManager(val tennisSite: TennisSite, val bookingsScheduler: BookingsScheduler) {
  
  val logger = Logger(getClass)
  
  def book(date: LocalDate = LocalDate.now, hour: Hours.Hours, court: Courts.Courts): Unit = {
    logger.trace(s"booking($date, $hour, $court)")
    if(canBookToday(date)) {
      tryToBook(date, hour, court)
    } else {
      scheduleBooking(date, hour, court)
    }
  }
  
  def canBookToday(date: LocalDate) = LocalDate.now.plusDays(3).isAfter(date)
  
  def tryToBook(date: LocalDate = LocalDate.now, hour: Hours.Hours, court: Courts.Courts) = {
    logger.debug(s"Trying to book today ($date, $hour, $court)")
    tennisSite.book(date, hour, court)
  }
  
  def scheduleBooking(date: LocalDate = LocalDate.now, hour: Hours.Hours, court: Courts.Courts) = {
    logger.debug(s"Will attempt to schedule later ($date, $hour, $court)")
    bookingsScheduler.scheduleBooking(this, date, hour, court)
  }
}

object BookingsManager {
  def apply(tennisSite: TennisSite, bookingsScheduler: BookingsScheduler) = new BookingsManager(tennisSite, bookingsScheduler)
}

class BookingsScheduler {
  val logger = Logger(getClass)
  val actorSystem = ActorSystem()
  val scheduler: Scheduler = actorSystem.scheduler
  implicit val executor = actorSystem.dispatcher
  
  def scheduleBooking(bookingsManager: BookingsManager, date: LocalDate = LocalDate.now, hour: Hours.Hours, court: Courts.Courts) = {
    logger.debug(s"Will attempt to schedule later ($date, $hour, $court)")
    def task = new Runnable {
      def run = {
        bookingsManager.book(date, hour, court)
      }
    }
    scheduler.schedule(
        initialDelay = Duration(5, MINUTES),
        interval = Duration(5, MINUTES),
        runnable = task)
  }
}
