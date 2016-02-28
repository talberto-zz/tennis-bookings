package config


/**
  * Created by trodriguez on 13/02/16.
  */
object TestConstants {
  lazy val Boot2Docker = TestConfig.configuration.getString("boot2docker.host").get

  lazy val ScaleFactor = TestConfig.configuration.getDouble("scaleFactor").get
}
