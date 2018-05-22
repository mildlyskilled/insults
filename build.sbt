name := """insult-me"""

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  lazy val akkaVersion = "2.5.8"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "io.spray" %% "spray-json" % "1.3.4",
    "org.scala-lang" % "jline" % "2.11.0-M3",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}
