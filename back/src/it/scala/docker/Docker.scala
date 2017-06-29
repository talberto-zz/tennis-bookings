package docker

import scala.languageFeature.postfixOps
import scala.sys.process._

object Docker {

  type ContainerId = String

  def runImage(image: String): ContainerId = {
    val containerId: String = s"docker run -d -P $image" !!

    containerId.trim.stripLineEnd
  }

  def removeContainer(containerId: ContainerId): Unit = {
    s"docker rm -f $containerId" !!
  }

  def findExposedPort(containerId: ContainerId, privatePort: Int): Int = {
    val exposedIpAndPort = s"docker port $containerId $privatePort" !!

    exposedIpAndPort.trim.stripLineEnd.split(":")(1).toInt
  }
}
