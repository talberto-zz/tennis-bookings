package models.site

import akka.actor.Actor
import models.BookingsApp.injector
import models.db.Booking
import net.codingwell.scalaguice.InjectorExtensions._
import play.api.Logger

object TennisSiteActor {
  case class Book(booking: Booking)
}

class TennisSiteActor extends Actor {
  val logger = Logger(getClass)
  val tennisSite = injector.instance[TennisSite]
  
  def receive = {
    case TennisSiteActor.Book(booking) => tennisSite.book(booking)
  }
}
