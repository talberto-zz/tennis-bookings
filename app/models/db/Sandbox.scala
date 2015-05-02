package models.db

import play.api.Play.current
import play.api.db._

import scala.slick.driver.PostgresDriver.simple._

/**
   * Holds the session for use in the repository
   */
object Sandbox {
  val ds = DB.getDataSource()
  val db = Database.forDataSource(ds)
}
