package models

import org.joda.time.DateTime

case class Comment(id: Long, creationDate: DateTime, comment: String)
