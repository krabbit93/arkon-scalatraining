name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"            %% "circe-core"      % "0.12.3",
  "io.circe"            %% "circe-parser"    % "0.12.3",
  "org.tpolecat"        %% "doobie-core"     % "0.8.8",
  "org.tpolecat"        %% "doobie-postgres" % "0.8.8",
  "org.sangria-graphql" %% "sangria"         % "2.0.0",
  "org.sangria-graphql" %% "sangria-circe"   % "1.3.0",
  "com.typesafe.akka"   %% "akka-http"       % "10.1.10",
  "com.typesafe.akka"   %% "akka-stream"     % "2.5.26",
  "org.typelevel"       %% "cats-core"       % "2.0.0",
  "org.scalatest"       %% "scalatest"       % "3.2.0" % "test"
)
