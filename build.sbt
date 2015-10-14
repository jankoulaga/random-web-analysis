name := """random-web-analysis"""

version := "1.0"


scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "net.databinder" %% "dispatch-nio" % "0.8.10",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test")
