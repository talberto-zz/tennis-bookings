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

object Status extends Enumeration {
  type Status = Value
  val PENDING, RESERVED = Value
  
  def unapply(v: Int) = Status(v)
}

import Status._

object Implicits {
  implicit def milisToDateTime(milis: Long): DateTime = new DateTime(milis)
  
  implicit def dateTimeToMilis(dateTime: DateTime): Long = dateTime.getMillis
  
  implicit def intToStatus(intStatus: Int): Status = Status(intStatus)
  
  implicit def statusToInt(status: Status): Int = status.id
  
  implicit def resReqApplyRawToApply(f: (Option[Int], Long, Int) => ReservationRequest): (Option[Int], DateTime, Status) => ReservationRequest = (id: Option[Int], date: DateTime, status: Status) => f(id, date, status)
  
  implicit def resReqApplyToApplyRaw(f: (Option[Int], DateTime, Status) => ReservationRequest): (Option[Int], Long, Int) => ReservationRequest = (id: Option[Int], date: Long, status: Int) => f(id, date, status)
  
  implicit def resReqUnapplyToUnapplyRaw(f: (ReservationRequest) => Option[(Option[Int], DateTime, Status)]): (ReservationRequest) => Option[(Option[Int], Long, Int)] = {
    (req: ReservationRequest) => {
      val option = f(req)
      option match {
        case Some((id, date, status)) => Some((id, date, status))
        case _ => None
      }
    }
  }
  
  implicit def resReqOptToResReqRawOpt(opt: Option[(Option[Int], DateTime, Status)]): Option[(Option[Int], Long, Int)] = {
    opt match {
      case Some((id, date, status)) => Some((id, date, status))
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
  
  implicit val requestStatusColumnType = MappedColumnType.base[Status, Int](
      { status => status.id },
      { integer => Status(integer) }
    )  
}

class ReservationRequest private(val id: Option[Int], val date: DateTime, val status: Status)

object ReservationRequest {
  def apply(id: Option[Int] = None, date: DateTime, status: Status): ReservationRequest = new ReservationRequest(id, date, status)
  
  def unapply(req: ReservationRequest): Option[(Option[Int], DateTime, Status)] = Option(req.id, req.date, req.status)
}

/**
 * @author tomas
 */
class ReservationRequests(tag: Tag) extends Table[ReservationRequest](tag, "requests") {
  import Converters._
    
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def date = column[DateTime]("date")
  def status = column[Status]("status")
  def * = (id.?, date, status) <> ((ReservationRequest.apply _).tupled, ReservationRequest.unapply)
}

object ReservationRequestRepository {
  import Sandbox.session
    
  private val requests = TableQuery[ReservationRequests]
  
  private val requestById = Compiled((id: ConstColumn[Int]) => requests filter(_.id === id))

  def findAll: Seq[ReservationRequest] = requests.list
  
  def findById(id: Int): Option[ReservationRequest] = requestById(id).firstOption
  
  def insert(resReq: ReservationRequest): ReservationRequest = {
    val id = (requests returning requests.map(_.id)) += resReq
    ReservationRequest(Some(id), resReq.date, resReq.status)
  }
  
  def update(resReq: ReservationRequest) = {
    requests.filter(_.id === resReq.id).map(r => (r.date, r.status)).update(resReq.date, resReq.status)
  }
  
  def delete(id: Int) = {
    requests.filter(_.id === id).delete
  }
  
  def delete(resReq: ReservationRequest): Unit = {
    delete(resReq.id.get)
  }
}