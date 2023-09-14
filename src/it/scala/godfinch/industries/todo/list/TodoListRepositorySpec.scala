package godfinch.industries.todo.list

import cats.effect.IO
import godfinch.industries.TestPostgresContainer
import munit.ScalaCheckEffectSuite
import org.scalacheck.effect.PropF

class TodoListRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import godfinch.industries.todo.todos.TodoGenerators._

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

  test("deleting a todo list") {
    PropF.forAllF(todoListGen) {
      todoList =>
        withPostgres {
          postgres =>
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)

            for {
              _ <- todoListRepository.insertTodoList(todoList)
              _ <- todoListRepository.deleteTodoList(todoList.id)
              result1 <- todoListRepository.getTodoList(todoList.id)
              _ = assertEquals(result1, None)
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