package godfinch.industries

import cats.effect.{IO, Resource}
import natchez.Trace.Implicits.noop
import skunk.implicits.toStringOps
import skunk.{Command, Session}
import cats.implicits._

object PostgresSuite extends ResourceSuite {

  override type Res = Resource[IO, Session[IO]]

  override def sharedResource: Resource[IO, Res] = Session.pooled[IO](
    host = "localhost",
    port = 5432,
    user = "postgres",
    database = "db",
    password = Some("my-password"),
    max = 10
  ).beforeAll(_.use(
    s => flushTables.traverse_(s.execute)
  ))

  val flushTables: List[Command[Void]] =
    List(
      "todo", "todos"
    ).map { table =>
      sql"DELETE FROM #$table".command
    }
}