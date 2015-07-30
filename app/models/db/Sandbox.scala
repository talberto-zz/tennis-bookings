package models.db


import slick.driver.PostgresDriver.api._

/**
   * Holds the session for use in the repository
   */
object Sandbox {
  val db = Database.forConfig("slick.dbs.default")
}
