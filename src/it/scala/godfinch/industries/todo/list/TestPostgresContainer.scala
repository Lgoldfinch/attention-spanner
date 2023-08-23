package godfinch.industries.todo.list

import cats.effect.IO
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import godfinch.industries.SqlMigrator
import munit.FunSuite
import org.testcontainers.utility.DockerImageName
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait TestPostgresContainer extends FunSuite with TestContainerForAll {

  override val containerDef: PostgreSQLContainer.Def = {
    PostgreSQLContainer.Def(
      dockerImageName = DockerImageName.parse("attention-spanner-postgres"),
      databaseName = "postgres",
      username = "postgres",
      password = "example"
    )
  }

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]


  withContainers(container =>
      new SqlMigrator[IO]
  )
}

