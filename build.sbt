name := """tennis-reservations"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    testOptions in IntegrationTest += Tests.Argument(TestFrameworks.ScalaTest, "-F", "2.0")
  )

libraryDependencies ++= Seq(
  cache,
  ws,
  jdbc,
  evolutions,
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "com.zaxxer" % "HikariCP" % "2.3.5",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.seleniumhq.selenium" % "selenium-java" % "2.39.0",
  "net.codingwell" % "scala-guice_2.11" % "4.0.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test,it",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test,it",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "it"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := StaticRoutesGenerator
