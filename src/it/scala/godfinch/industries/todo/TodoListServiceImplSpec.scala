package godfinch.industries.todo

import cats.MonadThrow
import cats.effect.IO
import cats.implicits._
import godfinch.industries.TestPostgresContainer
import godfinch.industries.attention.spanner.{TodoList, TodoListDb}
import godfinch.industries.todo.list.TodoListRepositoryImpl
import godfinch.industries.todo.todos.TodoRepositoryImpl
import munit.ScalaCheckEffectSuite
import org.scalacheck.Gen
import org.scalacheck.effect.PropF

class TodoListServiceImplSpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import godfinch.industries.todo.list.TodoListGenerators._
  import godfinch.industries.todo.todos.TodoGenerators._
  import godfinch.industries.utils.GeneralGenerators._

  test("Create todo list") {
    PropF.forAllF(todoListGen, Gen.listOfN(5, todoGen)) {
      (todoList, todos) =>
        withPostgres {
          postgres =>
            val todoRepository = new TodoRepositoryImpl[IO](postgres)
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
            val todoRepositoryService = new TodoListServiceImpl[IO](todoRepository, todoListRepository)

            for {
              _ <- todoRepositoryService.createTodoList(todoList.todoListName, todoList.expiryDate, todos)
              todoListResult <- todoListRepository.getTodoListByName(todoList.todoListName)
              todosResult <- todoListResult.traverse(todoList => todoRepository.getTodos(todoList.id))
              getTodoNames = todosResult.map(_.map(_.name))
              _ = assertEquals(getTodoNames, Some(todos.map(_.name)))
            } yield ()
        }
    }
  }

  test("Update todo list") {
    PropF.forAllF(todoListGen, nonEmptyListGen(todoDbGen), todoListGen, Gen.listOfN(3, todoGen)) {
     case (oldTodoList, todos, newTodoList, newTodos) =>
        withPostgres {
          postgres =>
            val todoRepository =     new TodoRepositoryImpl[IO](postgres)
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
            val todoListService =    new TodoListServiceImpl[IO](todoRepository, todoListRepository)
            val updatedNewTodoList = newTodoList.copy(oldTodoList.id)

            for {
              _ <- todoListRepository.insertTodoList(oldTodoList)
              _ <- todoRepository.insertTodos(todos.map(_.copy(todoListId = oldTodoList.id)))
              TodoListDb(id, name, expiryDate) = updatedNewTodoList
              _ <-              todoListService.updateTodoList(id, TodoList(name, expiryDate, newTodos))
              todoListResult <- todoListRepository.getTodoList(updatedNewTodoList.id)
              _ =  assertEquals(todoListResult, Some(updatedNewTodoList))
              todosResult    <- todoListResult.traverse(tl => todoRepository.getTodos(tl.id))
              getTodoNames = todosResult.map(_.map(_.name))
              _ =  assertEquals(getTodoNames, Some(newTodos.map(_.name)))
            } yield ()
        }
    }
  }
}
