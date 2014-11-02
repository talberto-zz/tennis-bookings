package models.db

import play.api.db._
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import org.joda.time.DateTime
import java.sql.Timestamp

object Sandbox {
    val ds = DB.getDataSource()
    implicit def session = Database.forDataSource(ds).createSession
}

object State extends Enumeration {
  type State = Value
  val PENDING, RESERVED = Value
  
  def unapply(v: Int) = State(v)
}

import State._

object Converters {
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
      { dateTime => new Timestamp(dateTime.getMillis) },
      { timestamp => new DateTime(timestamp) }
    )
  
  implicit val requestStateColumnType = MappedColumnType.base[State, Int](
      { state => state.id },
      { integer => State(integer) }
    )  
    
}

object Implicits {
  implicit def milisToDateTime(milis: Long): DateTime = new DateTime(milis)
  
  implicit def intToState(intState: Int): State = State(intState)
}

case class ReservationRequest(id: Int, date: DateTime, state: State)


/**
 * @author tomas
 */
class ReservationRequests(tag: Tag) extends Table[ReservationRequest](tag, "requests") {
  import Converters._
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def state = column[State]("state")
  def * = (id, date, state) <> (ReservationRequest.tupled, ReservationRequest.unapply)
}

object Dao {
  import Sandbox.session
    
  val requests = TableQuery[ReservationRequests]
  
  def allRequests = requests.list
}