package models.db

import play.api.db._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp
import scala.slick.driver.PostgresDriver.simple.Database

object Sandbox {
    val ds = DB.getDataSource()
    implicit def session = Database.forDataSource(ds).createSession
}

object Converters {
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
      { dt => new Timestamp(dt.getMillis) },
      { ts => new DateTime(ts) }
    )
}

case class Request(id: Int, date: DateTime)
/**
 * @author tomas
 */
class Requests(tag: Tag) extends Table[Request](tag, "requests") {
  import Converters.dateTimeColumnType
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def * = (id, date) <> (Request.tupled, Request.unapply)
}

object Dao {
  import Sandbox.session
    
  private val requests = TableQuery[Requests]
  
  def allRequests = requests.list
}