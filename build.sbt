name := """insult-me"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "org.scala-lang" % "jline" % "2.11.0-M3",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
