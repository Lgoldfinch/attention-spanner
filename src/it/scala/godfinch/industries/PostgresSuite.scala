package godfinch.industries

import cats.effect.{IO, Resource}
import natchez.Trace.Implicits.noop
import skunk.implicits.toStringOps
import skunk.{Command, Session}
import cats.implicits._
import godfinch.industries.todo.list.TodoListGenerators._
import godfinch.industries.todo.list.TodoListRepositoryImpl
import org.typelevel.log4cats.noop.NoOpLogger
import skunk._

object PostgresSuite extends ResourceSuite {

  override type Res = Resource[IO, Session[IO]]

  implicit val noopLogger = NoOpLogger[IO]
  override def sharedResource: Resource[IO, Res] = Session.pooled[IO](
    host = "database",
    port = 5432,
    user = "postgres",
    database = "postgres",
    password = Some("example"),
    max = 10
  ).beforeAll(_.use(
    s =>
      for {
//        _ <- new SqlMigrator("jdbc:postgresql://database:5432/postgres").run.void
        _ <- flushTables.traverse_(s.execute)
      } yield ()
  ))

  val flushTables: List[Command[Void]] =
    List(
      "todo", "todos"
    ).map { table =>
      sql"DELETE FROM #$table".command
    }

  test("inserting and retrieving todo list") { postgres =>
    forall(todoListGen) {
      todoList =>
        val todoListRepository = new TodoListRepositoryImpl[IO](postgres)

        for {
          beforeTest <- todoListRepository.getTodoList(todoList.id)
          _ <- todoListRepository.insertTodoList(todoList)
          afterTest <- todoListRepository.getTodoList(todoList.id)
        } yield expect.all(
          beforeTest.isEmpty,
          afterTest.isDefined
        )
    }
  }
}