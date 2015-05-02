package models.db

trait Repository[T] {

  /**
   * Retrieve all the T's
   */
  def list: Seq[T]
  
  def find(id: Long): Option[T]
  
  def save(entity: T): T
  
  def update(entity: T)
  
  def delete(id: Long)
}
