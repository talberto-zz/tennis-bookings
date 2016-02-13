package models.db

import slick.lifted.TableQuery

/**
 * Created by trodriguez on 01/05/15.
 */
object Queries {
   val bookings = TableQuery[Bookings]
   val comments = TableQuery[Comments]
 }
