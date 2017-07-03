package tech.trodriguez.tennisbookings.back.config

/**
  * Created by trodriguez on 13/02/16.
  */
object TestConstants {
  lazy val Boot2Docker = TestConfig.configuration.get[String]("boot2docker.host")

  lazy val ScaleFactor = TestConfig.configuration.get[Double]("scaleFactor")
}
