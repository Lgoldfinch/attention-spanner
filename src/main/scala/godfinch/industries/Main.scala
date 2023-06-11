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
import skunk._
import skunk.implicits._
import skunk.codec.all._
import natchez.Trace.Implicits.noop

object Main extends IOApp {

  val session: Resource[IO, Session[IO]] =
    Session.single( // (2)
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "attention-spanner-postgres",
      password = Some("example")
    )

//  def run(args: List[String]): IO[ExitCode] =
//    session.use { s => // (3)
//      for {
//        d <- s.unique(sql"select current_date".query(date)) // (4)
//        _ <- IO.println(s"The current date is $d.")
//      } yield ExitCode.Success
//    }

  override def run(args: List[String]): IO[ExitCode] = {

    val thePort = port"8080"
    val theHost = host"localhost"

    for {
      routes <- Routes.all
      _      <- EmberServerBuilder
      .default[IO]
      .withPort(thePort)
      .withHost(theHost)
      .withHttpApp(routes.orNotFound)
      .build <* Resource.eval(IO.println(s"Server started on: $theHost:$thePort"))
//      session <- dbSetup//.evalTap(checkPostgresConnection)
    } yield ()
  }.useForever

//  def checkPostgresConnection(
//                               postgres: Resource[IO, Session[IO]]
//                             ): IO[Unit] =
//    postgres.use { session =>
//      session.unique(sql"select version();".query(text)).flatMap { v =>
////        Logger[F].info(s"Connected to Postgres $v.")
//        IO.println(s"Connected to Postgres $v.")
//      }
//    }
}
