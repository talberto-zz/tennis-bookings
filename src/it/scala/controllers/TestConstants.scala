package controllers

import controllers.TestConfig.configuration

/**
  * Created by trodriguez on 13/02/16.
  */
object TestConstants {
  lazy val Boot2Docker = configuration.getString("boot2docker.host").get

  lazy val ScaleFactor = configuration.getDouble("scaleFactor").get
}
