package models

class AlreadyBookedException(msg: String = "", throwable: Throwable = null) extends RuntimeException {

  def this(booking: Booking) {
    this(s"The court [${booking.court}] is already booked at day [${booking.date}] and time [${booking.time}]")
  }
}