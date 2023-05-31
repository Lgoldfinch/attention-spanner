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

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val thePort = port"9000"
    val theHost = host"localhost"

    val dbSetup = Session.pooled[IO](
      host = "localhost",
      port = 5432,
      user = "jimmy",
      database = "world",
      password = Some("banana"),
      max = 16
    )

    for {
      routes <- Routes.all
      _      <- EmberServerBuilder
      .default[IO]
      .withPort(thePort)
      .withHost(theHost)
      .withHttpApp(routes.orNotFound)
      .build <* Resource.eval(IO.println(s"Server started on: $theHost:$thePort"))
      session <- dbSetup
      _ <- Resource.eval(checkPostgresConnection(session))
    } yield ()
  }.useForever

  def checkPostgresConnection(
                               postgres: Resource[IO, Session[IO]]
                             ): IO[Unit] =
    postgres.use { session =>
      session.unique(sql"select version();".query(text)).flatMap { v =>
//        Logger[F].info(s"Connected to Postgres $v.")
        IO.println(s"Connected to Postgres $v.")
      }
    }
}
