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
import skunk._
import skunk.implicits._
import skunk.codec.all._
import natchez.Trace.Implicits.noop
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {

  val session: Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO]( // (2)
      host = "database",
      port = 5432,
      user = "postgres",
      database = "postgres",
      password = Some("example"),
      max = 16
    )

  override def run(args: List[String]): IO[ExitCode] = {

    val thePort = port"8080"
    val theHost = host"0.0.0.0"

    for {
      postgres <- session.evalTap(checkPostgresConnection)
      implicit0(logger: Logger[IO]) <- Resource.eval(Slf4jLogger.create[IO])
      sqlMigrator = new SqlMigrator[IO]("jdbc:postgresql://database:5432/postgres")
      _ <- Resource.eval(sqlMigrator.run)
      routes <- Routes.all(postgres)

      _      <- EmberServerBuilder
      .default[IO]
      .withPort(thePort)
      .withHost(theHost)
      .withHttpApp(routes.orNotFound)
      .build <* Resource.eval(IO.println(s"Server started on: $theHost:$thePort"))
    } yield ()
  }.useForever

  def checkPostgresConnection(
                               postgres: Resource[IO, Session[IO]]
                             ): IO[Unit] =
    postgres.use { session =>
      session.unique(sql"select version();".query(text)).flatMap { v =>
        IO.println(s"Connected to Postgres $v. *******")
      }
    }
}
