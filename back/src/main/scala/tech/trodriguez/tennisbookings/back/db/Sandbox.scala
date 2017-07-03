package tech.trodriguez.tennisbookings.back.db


import slick.jdbc.PostgresProfile.api._

/**
   * Holds the session for use in the repository
   */
object Sandbox {
  val db = Database.forConfig("db.default")
}
