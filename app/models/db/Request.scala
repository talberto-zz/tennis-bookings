package models.db

import scala.slick.driver.PostgresDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
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

/**
 * @author tomas
 */
class Request(tag: Tag) extends Table[(Int,DateTime)](tag, "requests") {
    import Converters.dateTimeColumnType
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def * = (id, date)
}

object Dao {
    import Sandbox.session
    
  val requests = TableQuery[Request]
}