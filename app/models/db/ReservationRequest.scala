package models.db

import scala.language.implicitConversions // remove implicit conversion warnings
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

object Implicits {
  implicit def milisToDateTime(milis: Long): DateTime = new DateTime(milis)
  
  implicit def dateTimeToMilis(dateTime: DateTime): Long = dateTime.getMillis
  
  implicit def intToState(intState: Int): State = State(intState)
  
  implicit def stateToInt(state: State): Int = state.id
  
  implicit def resReqApplyRawToApply(f: (Int, Long, Int) => ReservationRequest): (Int, DateTime, State) => ReservationRequest = (id: Int, date: DateTime, state: State) => f(id, date, state)
  
  implicit def resReqApplyToApplyRaw(f: (Int, DateTime, State) => ReservationRequest): (Int, Long, Int) => ReservationRequest = (id: Int, date: Long, state: Int) => f(id, date, state)
  
  implicit def resReqUnapplyToUnapplyRaw(f: (ReservationRequest) => Option[(Int, DateTime, State)]): (ReservationRequest) => Option[(Int, Long, Int)] = {
    (req: ReservationRequest) => {
      val option = f(req)
      option match {
        case Some((id, date, state)) => Some((id, date, state))
        case _ => None
      }
    }
  }
  
  implicit def resReqOptToResReqRawOpt(opt: Option[(Int, DateTime, State)]): Option[(Int, Long, Int)] = {
    opt match {
      case Some((id, date, state)) => Some((id, date, state))
      case _ => None
    }
  }
}

import Implicits._

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

class ReservationRequest private(val id: Int, val date: DateTime, val state: State)

object ReservationRequest {
  def apply(id: Int, date: DateTime, state: State) = new ReservationRequest(id, date, state)
  
  def unapply(req: ReservationRequest): Option[(Int, DateTime, State)] = Option(req.id, req.date, req.state)
}

/**
 * @author tomas
 */
class ReservationRequests(tag: Tag) extends Table[ReservationRequest](tag, "requests") {
  import Converters._
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def state = column[State]("state")
  def * = (id, date, state) <> ((ReservationRequest.apply _).tupled, ReservationRequest.unapply)
}

object Dao {
  import Sandbox.session
    
  val requests = TableQuery[ReservationRequests]
  
  def allRequests = requests.list
}