import sbt._

class GAHelloWorld(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
  val scalatest  = "org.scalatest" % "scalatest_2.9.0" % "1.6.1"
  val akkaSTM    = akkaModule("stm")
  val akkaRemote = akkaModule("remote")
}
