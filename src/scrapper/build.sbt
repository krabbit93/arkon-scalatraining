name := "arkon-scalatraining-scrapper"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"                   %% "circe-core"      % "0.12.3",
  "io.circe"                   %% "circe-parser"    % "0.12.3",
  "ch.qos.logback"              % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
  "com.typesafe"                % "config"          % "1.4.0",
  "com.typesafe.akka"          %% "akka-http"       % "10.1.11",
  "com.typesafe.akka"          %% "akka-stream"     % "2.5.26",
  "org.typelevel"              %% "cats-core"       % "2.0.0",
  "org.typelevel"              %% "cats-effect"     % "2.0.0",
  "org.scalatest"              %% "scalatest"       % "3.2.0"  % "test",
  "org.scalacheck"             %% "scalacheck"      % "1.14.1" % "test"
)

scalaSource in Compile := baseDirectory.value / "main/scala"
scalaSource in Test := baseDirectory.value / "test/scala"
resourceDirectory in Compile := baseDirectory.value / "main/resources"
resourceDirectory in Test := baseDirectory.value / "test/resources"
