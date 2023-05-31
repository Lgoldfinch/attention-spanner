import Dependencies._

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "godfinch.industries"
ThisBuild / organizationName := "godfinch"

lazy val root = (project in file("."))
  .enablePlugins(Smithy4sCodegenPlugin)
  .settings(
    name := "attention-spanner",
    libraryDependencies ++=
      List.concat(
        Chimney,
        CirceExtras,
        DisneyStreaming.map(_ % smithy4sVersion.value), // mapping cannot be done within Dependencies
        Enumeratum,
        FlywayDb,
        Fs2Circe,
        Http4s,
        Logging,
        NewType,
        PureConfig,
        Skunk
      ) ++ List.concat(MCatsEffectTest, CatsEffectTest, MunitTest, ScalaCheckMunit, TestContainersScala)
    )

