import sbt._

class GAHelloWorld(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
  val scalatest  = "org.scalatest" % "scalatest" % "1.3"
  val akkaSTM    = akkaModule("stm")
  val akkaRemote = akkaModule("remote")
}
