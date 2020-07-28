name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"      %% "circe-core"   % "0.12.3",
  "io.circe"      %% "circe-parser" % "0.12.3",
  "org.tpolecat"  %% "doobie-core"  % "0.8.8",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "org.scalatest" %% "scalatest"    % "3.2.0" % "test"
)
