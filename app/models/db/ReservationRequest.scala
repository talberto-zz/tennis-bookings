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
  
  implicit def resReqApplyRawToApply(f: (Option[Int], Long, Int) => ReservationRequest): (Option[Int], DateTime, State) => ReservationRequest = (id: Option[Int], date: DateTime, state: State) => f(id, date, state)
  
  implicit def resReqApplyToApplyRaw(f: (Option[Int], DateTime, State) => ReservationRequest): (Option[Int], Long, Int) => ReservationRequest = (id: Option[Int], date: Long, state: Int) => f(id, date, state)
  
  implicit def resReqUnapplyToUnapplyRaw(f: (ReservationRequest) => Option[(Option[Int], DateTime, State)]): (ReservationRequest) => Option[(Option[Int], Long, Int)] = {
    (req: ReservationRequest) => {
      val option = f(req)
      option match {
        case Some((id, date, state)) => Some((id, date, state))
        case _ => None
      }
    }
  }
  
  implicit def resReqOptToResReqRawOpt(opt: Option[(Option[Int], DateTime, State)]): Option[(Option[Int], Long, Int)] = {
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

class ReservationRequest private(val id: Option[Int], val date: DateTime, val state: State)

object ReservationRequest {
  def apply(id: Option[Int] = None, date: DateTime, state: State): ReservationRequest = new ReservationRequest(id, date, state)
  
  def unapply(req: ReservationRequest): Option[(Option[Int], DateTime, State)] = Option(req.id, req.date, req.state)
}

/**
 * @author tomas
 */
class ReservationRequests(tag: Tag) extends Table[ReservationRequest](tag, "requests") {
  import Converters._
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def state = column[State]("state")
  def * = (id.?, date, state) <> ((ReservationRequest.apply _).tupled, ReservationRequest.unapply)
}

object ReservationRequestRepository {
  import Sandbox.session
    
  private val requests = TableQuery[ReservationRequests]
  
  private val requestById = Compiled((id: ConstColumn[Int]) => requests filter(_.id === id))

  def findAll: Seq[ReservationRequest] = requests.list
  
  def findById(id: Int): Option[ReservationRequest] = requestById(id).firstOption
  
  def insert(resReq: ReservationRequest): ReservationRequest = {
    val id = (requests returning requests.map(_.id)) += resReq
    ReservationRequest(Some(id), resReq.date, resReq.state)
  }
  
  def update(resReq: ReservationRequest) = {
    requests filter(_.id === resReq.id) map(r => (r.date, r.state)) update(resReq.date, resReq.state)
  }
  
  def delete(id: Int) = {
    requests filter(_.id === id) delete
  }
  
  def delete(resReq: ReservationRequest): Unit = {
    delete(resReq.id.get)
  }
}