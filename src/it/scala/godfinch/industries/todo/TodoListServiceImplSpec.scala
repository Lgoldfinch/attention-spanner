package godfinch.industries.todo

import cats.effect.IO
import godfinch.industries.TestPostgresContainer
import godfinch.industries.todo.list.TodoListRepositoryImpl
import godfinch.industries.todo.todos.TodoRepositoryImpl
import godfinch.industries.utils.GeneralGenerators.nonEmptyListGen
import munit.{ScalaCheckEffectSuite, ScalaCheckSuite}
import org.scalacheck.effect.PropF
import cats.implicits._

class TodoListServiceImplSpec extends TestPostgresContainer with ScalaCheckEffectSuite {
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
              todoListResult <- todoListRepository.getTodoListByName(todoList.todoListName)
              todosResult <- todoListResult.traverse(todoList => todoRepository.getTodos(todoList.id))
              _ = assertEquals(todosResult.map(_.map(_.name)), Some(todos.toList.map(_.name)))
            } yield ()
        }
    }
  }
}
