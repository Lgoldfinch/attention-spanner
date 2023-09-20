package godfinch.industries.todo.list

import cats.Show
import cats.effect.IO
import godfinch.industries.attention.spanner.{TodoDb, TodoListDb}
import godfinch.industries.{PostgresSuite, TestPostgresContainer}
import munit.ScalaCheckEffectSuite
import org.scalacheck.effect.PropF

object TodoListRepositorySpec extends PostgresSuite {
  import TodoListGenerators._

  implicit val showTodoDb: Show[TodoListDb] = new Show[TodoListDb] {
    override def show(t: TodoListDb): String = t.toString
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
              _ <- todoListRepository.updateTodoList(todoList.copy(todoListName = todoListName, expiryDate = expiryDate))
              result <- todoListRepository.getTodoList(todoList.id)
              _ = assertEquals(result.map(_.todoListName), Some(todoListName))
              _ = assertEquals(result.map(_.expiryDate), Some(expiryDate))
            } yield ()

        }
    }
  }
}