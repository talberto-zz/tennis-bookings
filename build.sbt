import com.typesafe.sbt.packager.docker.{Cmd, _}
import com.typesafe.sbt.web.Import.WebKeys.{stage => webStage, stagingDirectory => webStagingDirectory}
import sbt.Keys.version

lazy val root = (project in file("."))
  .aggregate(back, site)
  .settings(
    name := "tennis-bookings",
    version := Configuration.appVersion
  )

lazy val back = (project in file("back"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := """tennis-bookings-back""",
    version := Configuration.appVersion,
    scalaVersion := Configuration.scalaGlobalVersion,
    maintainer := "Tomas Rodriguez <rstomasalberto@gmail.com>",
    dockerUsername := Some(Configuration.dockerUsername),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:latest",
    dockerCommands := Seq(
      Cmd("FROM", dockerBaseImage.value),
      Cmd("MAINTAINER", maintainer.value),
      Cmd("WORKDIR", "/opt/docker"),
      Cmd("ADD", "opt", "/opt"),
      ExecCmd("RUN", "chown", "-R", "daemon: daemon", "."),
      Cmd("USER", "daemon"),
      ExecCmd("ENTRYPOINT", "bin/tennis-bookings-back")
    ),
    testOptions in IntegrationTest += Tests.Argument(TestFrameworks.ScalaTest, "-F", "2.0"),
    libraryDependencies ++= Seq(
      ws,
      jdbc,
      evolutions,
      guice,
      Dependencies.selenium,
      Dependencies.slick,
      Dependencies.hikariCp,
      Dependencies.postgres,
      Dependencies.scalaGuice,
      Dependencies.selenium % IntegrationTest,
      Dependencies.scalaTest % IntegrationTest,
      Dependencies.scalaTestPlus % IntegrationTest,
      Dependencies.akkaTestkit % IntegrationTest,
      Dependencies.scalaTest % IntegrationTest
    )
  )

lazy val site = (project in file("site"))
  .disablePlugins(Play, PlayLayoutPlugin, PlayScala)
  .enablePlugins(SbtWeb, DockerPlugin)
  .settings(
    name := """tennis-bookings-site""",
    version := Configuration.appVersion,
    maintainer := "Tomas Rodriguez <rstomasalberto@gmail.com>",
    dockerUsername := Some(Configuration.dockerUsername),
    dockerUpdateLatest := true,
    dockerBaseImage := "nginx:1.13.1-alpine",
    dockerCommands := Seq(
      Cmd("FROM", dockerBaseImage.value),
      Cmd("MAINTAINER", maintainer.value),
      Cmd("COPY", ".", "/usr/share/nginx/html")
    ),
    // docker:publishLocal depends on the web assets in the good directory
    (publishLocal in Docker) := ((publishLocal in Docker) dependsOn (webStage in Assets)).value,
    // Force the staging directory of Docker to be the staging directory of sbt-web in order to
    // have all the web assets in the Docker build context. NOTE: Docker build context is set to the
    // staging directory
    (stagingDirectory in Docker) := (webStagingDirectory in Assets).value
  )
