package godfinch.industries

import cats.effect._
import godfinch.industries.resources.{AppResources, MkHttpServer}
import org.http4s.implicits._
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    for {
      _ <- Resource.eval(IO.pure(args)) // discard unused args
      resources <- AppResources.make[IO]
      sqlMigrator = new SqlMigrator[IO]("jdbc:postgresql://localhost:5432/postgres")
      _ <- Resource.eval(sqlMigrator.run)
      routes <- Routes.all(resources.postgres)
      _ <- MkHttpServer[IO].newEmber(routes.orNotFound)
    } yield ()
  }.useForever


}
