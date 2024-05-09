ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "scala_project"
  )

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.3.5",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "com.typesafe.slick" %% "slick" % "3.5.0",
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
  "com.github.tminglei" %% "slick-pg" % "0.22.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.22.1"
)