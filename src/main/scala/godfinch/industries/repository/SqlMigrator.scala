//package godfinch.industries.repository
//
//import cats.effect.Sync
//import org.flywaydb.core.Flyway
//import org.typelevel.log4cats.Logger
//
//final class SqlMigrator[F[_]](implicit logger: Logger[F], S: Sync[F]) {
//
//  def run: F[Unit] =
//    for {
//       _ <- Flyway
//          .configure()
//          .dataSource(db.url, user, password)
//          .schemas(db.schema)
//          .load()
//          .migrate()
//      )
//      _ <- logger.info("Running the database migrations.")
//      _ <- logger.info(s"schema: [${db.schema}]; url: [${db.url}]")
//    } yield ()
//}
//
//object SqlMigrator {
//  def apply[F[_]: Logger: Sync](config: DbConfig): SqlMigrator[F] = new SqlMigrator[F](config)
//}
