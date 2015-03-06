package models

import play.api.db._
import play.api.Play.current

import scala.slick.driver.PostgresDriver.simple._

/**
   * Holds the session for use in the repository
   */
object Sandbox {
  val ds = DB.getDataSource()
  implicit def session = Database.forDataSource(ds).createSession
}