name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

lazy val shared   = project in file("src/shared")
lazy val api      = (project in file("src/api")).dependsOn(shared)
lazy val scrapper = (project in file("src/scrapper")).dependsOn(shared)
