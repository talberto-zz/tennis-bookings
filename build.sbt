lazy val root = (project in file("."))
  .aggregate(back, site)

lazy val back = (project in file("back"))
.enablePlugins(PlayScala)
.disablePlugins(PlayLayoutPlugin)
.configs(IntegrationTest)
.settings(Defaults.itSettings: _*)
.settings(
  name := """tennis-reservations-back""",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  testOptions in IntegrationTest += Tests.Argument(TestFrameworks.ScalaTest, "-F", "2.0"),
  libraryDependencies ++= Seq(
    ws,
    jdbc,
    evolutions,
    guice,
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "com.zaxxer" % "HikariCP" % "2.3.5",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    "org.seleniumhq.selenium" % "selenium-java" % "2.39.0",
    "net.codingwell" % "scala-guice_2.11" % "4.0.0",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test,it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
    "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % "it"
  )
)

import com.typesafe.sbt.packager.docker._

lazy val site = (project in file("site"))
.enablePlugins(DockerPlugin)
.settings(
  dockerBaseImage := "nginx"
)
