package models.db


import javax.inject.Singleton

import slick.driver.PostgresDriver.api._

/**
   * Holds the session for use in the repository
   */
@Singleton
class Sandbox {
  val db = Database.forConfig("db.default")
}
