name := """tennis-reservations"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

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
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "angularjs" % "1.3.15",
  "org.webjars" % "angular-ui-bootstrap" % "0.13.0",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
