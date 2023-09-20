import Dependencies.*

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "godfinch.industries"
ThisBuild / organizationName := "godfinch"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .enablePlugins(Smithy4sCodegenPlugin, JavaServerAppPackaging)
  .settings(
    name := "attention-spanner",
    Defaults.itSettings,
    IntegrationTest / fork := true,
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
        Monocle,
        PureConfig,
        Refined,
        Skunk
      ) ++ List.concat(MCatsEffectTest, CatsEffectTest, MunitTest, ScalaCheckMunit, TestContainersScala, Weaver),
        addCompilerPlugin ("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      scalacOptions ++= Seq(
          "-Ymacro-annotations",
      ),
    testFrameworks
      ++= List(new TestFramework("weaver.framework.CatsEffect"),
        new TestFramework("munit.Framework"))
)

Global / onChangedBuildSource := ReloadOnSourceChanges