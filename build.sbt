import Dependencies.*

ThisBuild / scalaVersion := "2.13.9"
ThisBuild / version := "1.0.0"
ThisBuild / organization := "godfinch.industries"
ThisBuild / organizationName := "godfinch"

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val IntegrationTest = config("it") extend(Test)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .enablePlugins(AshScriptPlugin, DockerPlugin, Smithy4sCodegenPlugin)
  .enablePlugins(AshScriptPlugin, DockerPlugin, ScalaJSPlugin, Smithy4sCodegenPlugin)
  .settings(
    name := "attention-spanner",
    dockerExposedPorts ++= Seq(8080),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    makeBatScripts  := Seq(),
    scalaJSUseMainModuleInitializer := true,
    Defaults.itSettings,
    IntegrationTest / fork := true,
    libraryDependencies ++=
      List.concat(
        Chimney,
        CirceExtras,
        DisneyStreaming.map(_ % smithy4sVersion.value), // mapping cannot be done within Dependencies
        Derevo,
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
      ) ++ List.concat(MCatsEffectTest, CatsEffectTest, MunitTest, ScalaCheckMunit, TestContainersScala, Weaver).map(_ % Test),
        addCompilerPlugin ("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      scalacOptions ++= Seq(
          "-Ymacro-annotations",
      ),
      testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )

Global / onChangedBuildSource := ReloadOnSourceChanges