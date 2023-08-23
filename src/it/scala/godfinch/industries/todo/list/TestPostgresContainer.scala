package godfinch.industries.todo.list

import cats.effect.std.CountDownLatch
import cats.effect.{IO, Resource}
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import godfinch.industries.SqlMigrator
import munit.CatsEffectSuite
import natchez.Trace.Implicits.noop
import org.testcontainers.utility.DockerImageName
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import skunk.{Command, Session}
import skunk.implicits.toStringOps
import skunk.Void
class TestPostgresContainer extends CatsEffectSuite with TestContainerForAll {

  override val containerDef: PostgreSQLContainer.Def =
    PostgreSQLContainer.Def(
      dockerImageName = DockerImageName.parse("postgres"),
      databaseName = "postgres",
      username = "postgres",
      password = "example"
    )


  private implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]


  def withPostgres[A](runTest: Session[IO] => IO[A]): IO[Unit] = {
    withContainers {
      container =>

        val session: Resource[IO, Session[IO]] = Session.single[IO](
          host = container.host,
          port = container.container.getFirstMappedPort,
          user = container.username,
          database = container.databaseName,
          password = Some(container.password)
        )

        for {
          _ <- new SqlMigrator[IO](container.container.getJdbcUrl).run
          deleteTodos: Command[Void] = sql"""DELETE FROM todo_list""".command
          _ <- session.use(_.execute(deleteTodos))
          _ <- session.use(postgres => runTest(postgres))
        } yield ()

    }
  }
}

