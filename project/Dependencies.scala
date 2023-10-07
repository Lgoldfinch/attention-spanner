import sbt.*
import smithy4s.codegen.Smithy4sCodegenPlugin.autoImport.smithy4sVersion

//noinspection TypeAnnotation
object Dependencies {
  private object Version {
    val Chimney      = "0.6.1"
    val CirceExtras  = "0.14.1" // version imported by Tapir
    val Derevo       = "0.13.0"
    val Enumeratum   = "1.7.0"
    val FlywayDb     = "9.11.0"
    val Fs2Circe     = "0.14.0"
    val Http4s       = "0.23.6" // look at version imported by Tapir
    val Log4Cats     = "2.2.0"
    val NewType      = "0.4.4"
    val Monocle      = "3.2.0"
    val PostgresJdbc            = "42.5.4" // Flyway needs this
    val PureConfig   = "0.17.4"
    val Skunk        = "0.6.0-RC2"

    // Test
    val MCatsEffectTest        = "1.0.7"
    val MunitDisciplineTest    = "1.0.9"
    val MunitTest              = "0.7.29"
    val CatsEffectTest         = "1.4.0"
    val Refined                = "0.11.0"
    val ScalacheckEffect       = "1.0.4"
    val ScalaTest              = "3.2.9"
    val ScalaTestPlus          = "3.2.2.0"
    val TestContainersScala    = "0.40.12"

    // Plugins
    val BetterMonadicFor = "0.3.1"
    val KindProjector    = "0.13.2"
    val OrganizeImports  = "0.6.0"
  }

  val Chimney = List(
    "io.scalaland" %% "chimney"
  ).map(_ % Version.Chimney)

  val CirceExtras = List(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-refined"
  ).map(_ % Version.CirceExtras)

  val Derevo = List(
    "tf.tofu" %% "derevo-core",
    "tf.tofu" %% "derevo-cats",
  ).map(_ % Version.Derevo)

  val DisneyStreaming = List(
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s",
    "com.disneystreaming.smithy4s" %% "smithy4s-http4s-swagger"
  )


  val Enumeratum = List(
  "com.beachape" %% "enumeratum",
  "com.beachape" %% "enumeratum-circe"
  ).map(_ % Version.Enumeratum)

  val FlywayDb = List(
  "org.flywaydb"   % "flyway-core" % Version.FlywayDb,
  "org.postgresql" % "postgresql"  % Version.PostgresJdbc
  )

  val Fs2Circe = List(
  "io.circe" %% "circe-fs2" % Version.Fs2Circe
  )

  val Http4s = List(
  "org.http4s" %% "http4s-ember-client",
  "org.http4s" %% "http4s-ember-server",
  "org.http4s" %% "http4s-circe"
  ).map(_ % Version.Http4s)

  val Logging = List(
    "org.typelevel"  %% "log4cats-slf4j",
    "org.typelevel"  %% "log4cats-noop"
  ).map(_ % Version.Log4Cats)

  val NewType = List(
  "io.estatico" %% "newtype" % Version.NewType
  )

  val Monocle = List(
  "dev.optics" %% "monocle-core" % Version.Monocle
  )

  val PureConfig = List(
  "com.github.pureconfig" %% "pureconfig"      % Version.PureConfig,
  "com.github.pureconfig" %% "pureconfig-cats" % Version.PureConfig
  )

  val Refined = List("eu.timepit" %% "refined").map(_ % Version.Refined)

  val Skunk = List("org.tpolecat" %% "skunk-core").map(_ % Version.Skunk)

  val CatsEffectTest = List(
  "org.typelevel" %% "cats-effect-testing-scalatest" % Version.CatsEffectTest
  )

  val MCatsEffectTest = List(
    "org.typelevel" %% "munit-cats-effect-3" % Version.MCatsEffectTest
  )

  val MunitTest = List(
  "org.scalameta" %% "munit"                 % Version.MunitTest,
  "org.scalameta" %% "munit-scalacheck"      % Version.MunitTest,
  "com.beachape"  %% "enumeratum-scalacheck" % Version.Enumeratum
  )

  val ScalaCheckMunit = List(
  "org.typelevel" %% "scalacheck-effect-munit" % Version.ScalacheckEffect
  )

  val TestContainersScala = List(
    "com.dimafeng" %% "testcontainers-scala-munit"      % Version.TestContainersScala,
    "com.dimafeng" %% "testcontainers-scala-postgresql" % Version.TestContainersScala
  )

  val Weaver = List(
    "com.disneystreaming" %% "weaver-cats",
    "com.disneystreaming" %% "weaver-scalacheck"
  ).map(_ % "0.8.3")

  /** Plugins */
  val BetterMonadicFor = "com.olegpy" %% "better-monadic-for" % Version.BetterMonadicFor
  val KindProjector =
    "org.typelevel" %% "kind-projector" % Version.KindProjector cross CrossVersion.full
  val OrganizeImports = "com.github.liancheng" %% "organize-imports" % Version.OrganizeImports
}
