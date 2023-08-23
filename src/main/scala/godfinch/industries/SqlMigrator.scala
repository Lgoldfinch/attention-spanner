package godfinch.industries

import cats.effect.Sync
import cats.implicits._
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger

final class SqlMigrator[F[_]: Sync](implicit logger: Logger[F]) {
  def run: F[Unit] = {
    val url = "jdbc:postgresql://database:5432/postgres"

    for {
      _ <- Sync[F].blocking(Flyway
        .configure()
        .dataSource(url, "postgres", "example")
        .load()
        .migrate())
      _ <- logger.info("Running the database migrations.") >>
            logger.info(s"schema: postgres; url: $url")
    } yield ()
  }
}