package godfinch.industries.todo.list

import cats.effect.IO
import godfinch.industries.attention.spanner.{ExpiryDate, NonEmptyStringFormat, TodoDb, TodoListDb, TodoListId, TodoListName}
import godfinch.industries.todo.todos.TodoRepositoryImpl
import godfinch.industries.utils.NonEmptyStringFormatR
import munit.ScalaCheckEffectSuite
import org.scalacheck.Gen
import org.scalacheck.effect.PropF
import smithy4s.Timestamp

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import java.util.Date

class TodoListRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import godfinch.industries.todo.TodoGenerators._

  test("inserting and retrieving a todo list") {
    PropF.forAllF(todoListGen) {
      todoList =>
        withPostgres {
          postgres =>
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)

            for {
              _ <- todoListRepository.insertTodoList(todoList)
              res <- todoListRepository.getTodoList(todoList.id)
            } yield assertEquals(res, Some(todoList))
        }
    }
  }

  test("deleting a todo list should delete the todo list and it's incumbent todos") {
    PropF.forAllF(todoListGen) {
      todoList =>
        withPostgres {
          postgres =>
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
            val todoRepository = new TodoRepositoryImpl[IO](postgres)

            for {
              _ <- todoListRepository.insertTodoList(todoList)
              _ <- todoListRepository.deleteTodoList(todoList.id)
              result1 <- todoListRepository.getTodoList(todoList.id)
              result2 <- todoRepository.getTodos(todoList.id)
              _ = assertEquals(result1, None)
              _ = assertEquals(result2, List.empty[TodoDb])
            } yield ()
        }
    }
  }

  test("updating a todo list") {
    PropF.forAllF(todoListGen, expiryDateGen, todoListNameGen) {
      (todoList, expiryDate, todoListName) =>
        withPostgres {
          postgres =>
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)

            for {
              _ <- todoListRepository.insertTodoList(todoList)
              _ <- todoListRepository.updateTodoList(todoList.copy(todoListName = todoListName, expiryDate = expiryDate))
              result <- todoListRepository.getTodoList(todoList.id)
              _ = assertEquals(result.map(_.todoListName), Some(todoListName))
              _ = assertEquals(result.map(_.expiryDate), Some(expiryDate))
            } yield ()

        }
    }
  }
}