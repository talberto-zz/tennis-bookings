import sbt._

object Configuration {
  lazy val appVersion = "1.0-SNAPSHOT"

  lazy val scalaGlobalVersion = "2.12.2"

  lazy val dockerUsername = "talberto"
}


object Dependencies {

  object Versions {
    val akka = "2.5.3"
    val selenium = "2.39.0"
    val slick = "3.2.0"
    val hikariCp = "2.3.5"
    val postgres = "9.4-1201-jdbc41"
    val scalaTest = "3.0.1"
    val scalaTestPlus = "3.0.0"
    val scalaGuice = "4.1.0"
    val playWsStandalone = "1.0.0"
    val dockerClient = "8.8.0"
    val kubernetesClient = "2.5.2"
  }

  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Versions.akka
  val selenium = "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium
  val slick = "com.typesafe.slick" %% "slick" % Versions.slick
  val slickHikariCp = "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick
  val hikariCp = "com.zaxxer" % "HikariCP" % Versions.hikariCp
  val postgres = "org.postgresql" % "postgresql" % Versions.postgres
  val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest
  val scalaTestPlus = "org.scalatestplus.play" %% "scalatestplus-play" % Versions.scalaTestPlus
  val scalaGuice = "net.codingwell" %% "scala-guice" % Versions.scalaGuice
  val playWsStandalone = "com.typesafe.play" %% "play-ahc-ws-standalone" % Versions.playWsStandalone
  val playWsStandaloneJson = "com.typesafe.play" %% "play-ws-standalone-json" % Versions.playWsStandalone
  val dockerClient = "com.spotify" % "docker-client" % Versions.dockerClient
  val kubernetesClient = "io.fabric8" % "kubernetes-client" % Versions.kubernetesClient
}
