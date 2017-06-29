lazy val root = (project in file("."))
  .aggregate(back, site)

lazy val akkaVersion = "2.5.3"

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
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it"
    )
  )

import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.web.Import.WebKeys.{assets, stagingDirectory => webStagingDirectory}

lazy val site = (project in file("site"))
  .disablePlugins(PlayLayoutPlugin)
  .enablePlugins(SbtWeb)
  .enablePlugins(DockerPlugin)
  .settings(
    maintainer := "Tomas Rodriguez <rstomasalberto@gmail.com>",
    dockerCommands := Seq(
      Cmd("FROM", "nginx:1.13.1-alpine"),
      Cmd("MAINTAINER", maintainer.value),
      Cmd("COPY", ".", "/usr/share/nginx/html")
    ),
    // docker:publishLocal depends on the web assets in the good directory
    (publishLocal in Docker) := ((publishLocal in Docker) dependsOn (assets in Assets)).value,
    // Force the staging directory of Docker to be the staging directory of sbt-web in order to
    // have all the web assets in the Docker build context. NOTE: Docker build context is set to the
    // staging directory
    (stagingDirectory in Docker) := (webStagingDirectory in Assets).value
  )
