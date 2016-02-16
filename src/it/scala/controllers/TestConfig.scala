package controllers

import com.typesafe.config.ConfigFactory
import play.api.Configuration

object TestConfig {

  val configuration = Configuration(ConfigFactory.load("application"))
}
