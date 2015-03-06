package models

import SlickConverters._
import Sandbox.session

import org.joda.time.DateTime

import play.api.db._
import play.api.Play.current
import play.api.Logger

import java.sql.Timestamp

import scala.language.implicitConversions // remove implicit conversion warnings
import scala.slick.driver.PostgresDriver.simple._

class BookingWithComments {

}

class BookingWithCommentsRepository {
  val bookings = Queries.bookings
  val comments = Queries.comments
  
  val tupledJoin: Query[(Comments, Bookings), (Comments#TableElementType, Bookings#TableElementType), Seq] = comments join bookings on (_.bookingId === _.id)
  
  def findByBooking(id: Long) = {
  }
}
