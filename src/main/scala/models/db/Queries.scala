package models.db

import slick.lifted.TableQuery

/**
 * Created by trodriguez on 01/05/15.
 */
object Queries {
   val reservations = TableQuery[Reservations]
   val comments = TableQuery[Comments]
 }
