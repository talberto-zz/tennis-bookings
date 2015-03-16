package models

import play.api.Play

trait DefaultWithBookingsRepository extends WithBookingsRepository {
  val bookingsRepository = BookingsRepository()
}

trait DefaultWithCommentsRepository extends WithCommentsRepository {
  val commentsRepository = CommentsRepository()
}

trait WithCurrentPlayConfiguration extends WithPlayConfiguration {
  lazy val conf = Play.current.configuration
}

trait DefaultWithTennisSite extends WithTennisSite {
  val tennisSite = new TennisSite with WithCurrentPlayConfiguration
}

trait DefaultWithBookingsManager extends WithBookingsManager {
  val bookingsManager = new BookingsManager with DefaultWithBookingsRepository with DefaultWithCommentsRepository with DefaultWithTennisSite
}

trait AppRegistry extends DefaultWithBookingsManager
  with DefaultWithTennisSite
  with WithCurrentPlayConfiguration
  with DefaultWithCommentsRepository
  with DefaultWithBookingsRepository {

}
