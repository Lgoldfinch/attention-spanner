package godfinch.industries.todo.todos

import cats.data.NonEmptyList
import cats.effect.IO
import godfinch.industries.TestPostgresContainer
import godfinch.industries.attention.spanner.{TodoList, TodoListDb, TodoListId}
import godfinch.industries.todo.list.TodoListRepositoryImpl
import godfinch.industries.utils.GeneralGenerators.nonEmptyListGen
import munit.ScalaCheckEffectSuite
import org.scalacheck.effect.PropF

import java.util.UUID

class TodoRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import TodoGenerators._
  import godfinch.industries.todo.list.TodoListGenerators._

  test("inserting todo and retrieving by todoListId should only return the relevant todos.") {
      PropF.forAllF(nonEmptyListGen(todoDbGen), todoDbGen, todoListGen, todoListGen) {
        (todos, todo, todoList1, todoList2) =>
          withPostgres {
            postgres =>



              val todosWithMatchingTodoListId = {
                val todoListId = todos.head.todoListId
                todos.map(_.copy(todoListId = todoListId))
              }

              val todoRepository = new TodoRepositoryImpl[IO](postgres)
              val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
              val todoListModified1 = todoList1.copy(id = todos.head.todoListId)
              val todoListModified2 = todoList2.copy(id = todo.todoListId)

              for {
                _ <- todoListRepository.insertTodoList(todoListModified1)
                _ <- todoListRepository.insertTodoList(todoListModified2)
                _ <- todoRepository.insertTodos(todosWithMatchingTodoListId)
                _ <- todoRepository.insertTodos(NonEmptyList.of(todo))
                todoResult1 <- todoRepository.getTodos(todos.head.todoListId)
                todoResult2 <- todoRepository.getTodos(todo.todoListId)
                _ = assertEquals(todoResult1, todos.toList)
                _ = assertEquals(todoResult2, List(todo))
              } yield ()
          }
      }
  }

}
