package godfinch.industries

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.server._
import com.comcast.ip4s._
import skunk.Session
import natchez.Trace.Implicits.noop
import cats.effect._
import skunk._
import skunk.implicits._
import skunk.codec.all._
import cats.effect._
import cats.effect.unsafe.implicits.global
import godfinch.industries.resources.AppResources
import skunk._
import skunk.implicits._
import skunk.codec.all._
import natchez.Trace.Implicits.noop
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val thePort = port"8080"
    val theHost = host"0.0.0.0"
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    for {
      resources <- AppResources.make[IO]
      sqlMigrator = new SqlMigrator[IO]("jdbc:postgresql://database:5432/postgres")
      _ <- Resource.eval(sqlMigrator.run)
      routes <- Routes.all(resources.postgres)

      _      <- EmberServerBuilder
      .default[IO]
      .withPort(thePort)
      .withHost(theHost)
      .withHttpApp(routes.orNotFound)
      .build <* Resource.eval(IO.println(s"Server started on: $theHost:$thePort"))
    } yield ()
  }.useForever


}
