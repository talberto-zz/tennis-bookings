package models.db

import org.joda.time.DateTime

case class Comment(id: Long, creationDate: DateTime, text: String, screenshot: Option[String] = None, bookingId: Long)
