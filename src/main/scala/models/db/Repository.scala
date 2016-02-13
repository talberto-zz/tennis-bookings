package models.db

import scala.concurrent.Future

trait Repository[T] {

  /**
   * Retrieve all the T's
   */
  def list: Future[Seq[T]]
  
  def find(id: Long): Future[Option[T]]
  
  def save(entity: T): Future[T]
  
  def update(entity: T): Future[Int]
  
  def delete(id: Long): Future[Int]
}
