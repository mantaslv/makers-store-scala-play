name := """play-scala-the-makers-store"""
organization := "com.makers"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  guice,
  "org.playframework" %% "play-slick"            % "6.1.1",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "org.postgresql" % "postgresql" % "42.7.2", // PostgreSQL driver
  "org.mindrot" % "jbcrypt" % "0.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test,
  "org.scalatestplus" %% "selenium-4-21" % "3.2.19.0" % "test"
)

// Set the config.file system property for tests
Test / fork := true
Test / javaOptions ++= Seq("-Dconfig.file=conf/test.conf")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.makers.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.makers.binders._"
