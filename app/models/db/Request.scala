package models.db

import scala.slick.driver.PostgresDriver.simple._

/**
 * @author tomas
 */
class Request(tag: Tag) extends Table[(Int,String)](tag, "requests") {
  def id = column[Int]("id")
  def date = column[String]("date")
  def * = (id, date)
}