package models

import com.google.inject.Guice

object BookingsApp {

  val injector = Guice.createInjector(new BookingsModule())
}