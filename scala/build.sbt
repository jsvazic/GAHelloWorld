name := "GA Hello World"

version := "1.1"

organization := "net.auxesia"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "se.scalablesolutions.akka" % "akka-actor" % "1.2"
