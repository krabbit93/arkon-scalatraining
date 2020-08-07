name := "arkon-scalatraining-shared"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"  % "3.2.0"  % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"
)

scalaSource in Compile := baseDirectory.value / "main/scala"
scalaSource in Test := baseDirectory.value / "test/scala"
resourceDirectory in Compile := baseDirectory.value / "main/resources"
resourceDirectory in Test := baseDirectory.value / "test/resources"
