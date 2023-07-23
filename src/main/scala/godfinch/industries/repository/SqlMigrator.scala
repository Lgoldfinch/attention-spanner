package godfinch.industries.repository

import cats.effect.Sync
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import cats.implicits._

final class SqlMigrator[F[_]](implicit logger: Logger[F], S: Sync[F]) {
  def run: F[Unit] = {
    val url = "jdbc:postgresql://database:5432/postgres"

    for {
      _ <- S.blocking(Flyway
        .configure()
        .dataSource(url, "postgres", "example")
        .load()
        .migrate())
      _ <- logger.info("Running the database migrations.") >>
            logger.info(s"schema: postgres; url: $url")
    } yield ()
  }
}