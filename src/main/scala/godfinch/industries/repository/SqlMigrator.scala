package godfinch.industries.repository

import cats.effect.Sync
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import cats.implicits._

final class SqlMigrator[F[_]](implicit logger: Logger[F], S: Sync[F]) {
  def run: F[Unit] = {
    for {
      _ <- logger.info("Hello?")
      _ <- S.delay(Flyway
        .configure()
        .dataSource("jdbc:postgresql://database:5432/postgres", "postgres", "example")
//                  .schemas("attention-spanner")
        .load()
        .migrate())
      _ <- logger.info("Running the database migrations. ^^^^^^^^^^^^^^^^^") >>
            logger.info(s"schema: postgres; url: jdbc:postgresql://database:5432/postgres")
    } yield ()
  }
}