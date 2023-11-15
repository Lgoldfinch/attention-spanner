package godfinch.industries.resources

import cats.effect.kernel.Temporal
import cats.effect.std.Console
import cats.effect.{MonadCancelThrow, Resource}
import fs2.io.net.Network
import natchez.Trace.Implicits.noop
import org.typelevel.log4cats.Logger
import skunk.Session
import skunk.codec.all.text
import skunk.implicits._
import cats.implicits._

sealed abstract class AppResources[F[_]](
                                          val postgres: Resource[F, Session[F]]
                                        )

object AppResources {
  def make[F[_]: Console: Logger: MonadCancelThrow: Network: Temporal]: Resource[F, AppResources[F]] = {

    def checkPostgresConnection(
                                 postgres: Resource[F, Session[F]]
                               ): F[Unit] =
      postgres.use { session =>
        session.unique(sql"select version();".query(text)).flatMap { v =>
          Logger[F].info(s"Connected to Postgres $v.")
        }
      }

    def mkPostgresResource: Resource[F, Resource[F, Session[F]]] =
      Session.pooled[F](
        host = "localhost",
        port = 5432,
        user = "postgres",
        database = "attention-spanner-postgres",
        password = Some("password"),
        max = 16
      ).evalTap(checkPostgresConnection)

    mkPostgresResource.map(new AppResources[F](_) {})
  }




}
