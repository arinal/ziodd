import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version      := "0.0.1"
ThisBuild / organization := "io.lamedh"

lazy val core = (project in file("modules/core"))
  .settings(
    name := "core",
    commonSettings,
    libraryDependencies ++= layer.core
  )

lazy val infraHttpClient = (project in file("modules/infra.client.http"))
  .settings(
    name := "infra.client.http",
    commonSettings,
  )
  .dependsOn(core)
  
lazy val infraRepoMemory = (project in file("modules/infra.repo.memory"))
  .settings(
    name := "infra.repo.memory",
    commonSettings,
  )
  .dependsOn(core)
  
lazy val appApiHttpSched = (project in file("modules/app.api.http.sched"))
  .settings(
    name := "app.api.http.sched",
    commonSettings,
    libraryDependencies ++= layer.appApiHttpSched
  )
  .dependsOn(core)
  .dependsOn(infraHttpClient, infraRepoMemory)
  
lazy val appCliSched = (project in file("modules/app.cli.sched"))
  .settings(
    name := "app.cli.sched",
    commonSettings,
    libraryDependencies ++= layer.appCliSched
  )
  .dependsOn(core)
  .dependsOn(infraHttpClient, infraRepoMemory)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-Ywarn-unused:imports")
)
