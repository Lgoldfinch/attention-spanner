package godfinch.industries.todo.list

import cats.effect.IO
import godfinch.industries.PostgresSuite._
import godfinch.industries.TestPostgresContainer
import munit.ScalaCheckEffectSuite
import org.scalacheck.effect.PropF

object TodoListRepositorySpec  {
  import TodoListGenerators._

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

class TodoListRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import TodoListGenerators._

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
              _ <- todoListRepository.updateTodoList(todoList.copy(name = todoListName, expiryDate = expiryDate))
              result <- todoListRepository.getTodoList(todoList.id)
              _ = assertEquals(result.map(_.name), Some(todoListName))
              _ = assertEquals(result.map(_.expiryDate), Some(expiryDate))
            } yield ()

        }
    }
  }
}