import Dependencies.{DisneyStreaming, *}
//import sbtcrossproject.{crossProject, CrossType}

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val IntegrationTest = config("it") extend(Test)

lazy val commonSettings  = {
    organization := "godfinch"
    organizationName := "godfinch.industries"
    version := "1.0.0"
    scalaVersion := "2.13.9"
}

//lazy val shared = (
//  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared")).settings(commonSettings)
//)

val backendDependencies = libraryDependencies ++=
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
  ) ++ List.concat(MCatsEffectTest, CatsEffectTest, MunitTest, ScalaCheckMunit, TestContainersScala, Weaver).map(_ % Test)

//lazy val shared = (
//  crossProject(JSPlatform, JVMPlatform) in file("shared")).settings(
//  commonSettings,
//
//)

lazy val backend = (project in file("backend"))
  .settings(
      commonSettings,
      name := "attention-spanner-backend",
      backendDependencies,
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      scalacOptions ++= Seq(
            "-Ymacro-annotations",
      ),
      testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  ).enablePlugins(Smithy4sCodegenPlugin)

//lazy val frontend = (project in file("frontend")).settings(
//  commonSettings,
//  name := "attention-spanner-frontend",
//   Build a js dependencies file
//  skip in packageJsDependencies := false,
//  jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
//   Put the jsdeps file on a place reachable for the server
//  crossTarget in(Compile, packageJSDependencies) := (resourceManaged in Compile).value,
//  testFrameworks += new TestFramework("utest.runner.Framework"),
//  libraryDependencies ++= Seq(
//    "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
//    "com.lihaoyi" %%% "utest" % utestV % Test
//).enablePlugins(ScalaJSPlugin)
//

//  .settings(
//      resources / Compile += (fastOptJs in (frontend, Compile)).value.data
//  )

//lazy val frontend = (project in file("backend"))
//  .settings(
//      name := "attention-spanner-frontend",
//  ).enablePlugins(ScalaJSPlugin)
//  .settings(commonSettings)
//  .settings(
//      skip in packageJsDependencies := false,
//      jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
//
//       Put the jsdeps file on a place reachable for the server
//      crossTarget / (Compile, packageJSDependencies) := (resourceManaged in Compile).value,
//      testFrameworks += new TestFramework("utest.runner.Framework"),
//      libraryDependencies ++= Seq(
//          "org.scala-js" %%% "scalajs-dom" % scalaJsDomV,
//          "com.lihaoyi" %%% "utest" % utestV % Test
//      )
//  )
//  .dependsOn(sharedJs)



lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .enablePlugins(AshScriptPlugin, DockerPlugin, Smithy4sCodegenPlugin)
  .settings(
    name := "attention-spanner",
    dockerExposedPorts ++= Seq(8080),
    dockerUpdateLatest := true,
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    makeBatScripts  := Seq(),
    scalaJSUseMainModuleInitializer := true,
    Defaults.itSettings,
    IntegrationTest / fork := true
  )


Global / onChangedBuildSource := ReloadOnSourceChanges