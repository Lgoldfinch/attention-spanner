package godfinch.industries.todo

import cats.effect.IO
import godfinch.industries.TestPostgresContainer
import godfinch.industries.todo.list.TodoListRepositoryImpl
import godfinch.industries.todo.todos.{TodoRepository, TodoRepositoryImpl}
import godfinch.industries.utils.GeneralGenerators.nonEmptyListGen
import munit.ScalaCheckSuite
import org.scalacheck.effect.PropF

class TodoListServiceImplSpec extends TestPostgresContainer with ScalaCheckSuite {
  import godfinch.industries.todo.list.TodoListGenerators._
  import godfinch.industries.todo.todos.TodoGenerators._

  test("Creating a todo list should store todo list and related todos") {
    PropF.forAllF(todoListGen, nonEmptyListGen(todoGen)) {
      (todoList, todos) =>
        withPostgres {
          postgres =>
            val todoRepository = new TodoRepositoryImpl[IO](postgres)
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
            val todoRepositoryService = new TodoListServiceImpl[IO](todoRepository, todoListRepository)

            for {
              _ <- todoRepositoryService.createTodoList(todoList.todoListName, todoList.expiryDate, todos.toList)
              _ <- todoRepositoryService.getTodoList()
            } yield ()
        }
    }
  }
}
