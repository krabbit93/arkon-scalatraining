name := "arkon-scalatraining-scrapper"

version := "0.1"

scalaVersion := "2.13.3"

scalaSource in Compile := baseDirectory.value / "main/scala"
scalaSource in Test := baseDirectory.value / "test/scala"
resourceDirectory in Compile := baseDirectory.value / "main/resources"
