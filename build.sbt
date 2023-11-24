import Dependencies.Version.Scala
import Dependencies.{DisneyStreaming, *}
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

lazy val IntegrationTest = config("it") extend(Test)

lazy val commonSettings  = {
    organization := "godfinch"
    organizationName := "godfinch.industries"
    version := "1.0.0"
    scalaVersion := Version.Scala
}

//lazy val shared =
//  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("modules/shared"))
//    .jsSettings(commonSettings)
//    .jvmSettings(commonSettings)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val root =
  project.in(file(".")).aggregate(frontend, backend, shared.js, shared.jvm)

lazy val backend = project.in(file("modules/backend"))
  .dependsOn(sharedJvm)
  .settings(commonBuildSettings)
//  .settings(commonSettings)
  .settings(
    backendDependencies,
    testDependencies,
    addCompilerPlugin(KindProjector),
    addCompilerPlugin(BetterMonadicFor),
    addCompilerPlugin(OrganizeImports),
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
    )
  )
  .settings(
  )
    .configs(IntegrationTest)
    .enablePlugins(AshScriptPlugin, DockerPlugin, Smithy4sCodegenPlugin)
    .settings(
      dockerExposedPorts ++= Seq(8080),
      dockerUpdateLatest := true,
      dockerBaseImage := "openjdk:11-jre-slim-buster",
      makeBatScripts  := Seq(),
      Defaults.itSettings,
      IntegrationTest / fork := true
    )

lazy val frontend = project.in(file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared.js)
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(
    // Insert dependencies
    Test / jsEnv := new JSDOMNodeJSEnv()
  ).settings(commonBuildSettings)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/shared"))
//  .jvmSettings(Dependencies.shared)
//  .jsSettings(Dependencies.shared)
  .jsSettings(commonBuildSettings)
  .jvmSettings(commonBuildSettings)

lazy val fastOptCompileCopy = taskKey[Unit]("")

val jsPath = "modules/backend/src/main/resources"

fastOptCompileCopy := {
  val source = (frontend / Compile / fastOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "dev.js"
  )
}

lazy val fullOptCompileCopy = taskKey[Unit]("")

fullOptCompileCopy := {
  val source = (frontend / Compile / fullOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "prod.js"
  )

}

lazy val commonBuildSettings: Seq[Def.Setting[?]] = Seq(
  scalaVersion := Version.Scala
)

addCommandAlias("runDev", ";fastOptCompileCopy; backend/reStart --mode dev")
addCommandAlias("runProd", ";fullOptCompileCopy; backend/reStart --mode prod")


Global / onChangedBuildSource := ReloadOnSourceChanges