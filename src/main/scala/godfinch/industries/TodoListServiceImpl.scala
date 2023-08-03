package godfinch.industries

import cats.{Applicative, Monad}
import cats.effect.kernel.Clock
import cats.effect.std.Console
import cats.implicits._
import godfinch.industries.attention.spanner._
import godfinch.industries.repository.TodoRepository
import godfinch.industries.repository.model.TodoListRepository
import smithy4s.Timestamp

import java.util.UUID

final class TodoListServiceImpl[F[_]: Monad](todoRepository: TodoRepository[F], todoListRepository: TodoListRepository[F]) extends TodoListService[F] {

  override def createTodoList(todoListName: TodoListName, expiryDate: ExpiryDate, todos: List[Todo]): F[Unit] = {
      val todoListId = TodoListId(UUID.randomUUID())
      for {
        _ <- todoListRepository.insertTodoList(TodoListDb(todoListId, todoListName, expiryDate))
        todosWithIds = todos.map{ todo =>
          val todoId = UUID.randomUUID()
          TodoDb(todoId, todoListId, todo.name, todo.isCompleted)
        }.toNel
        _ <- todosWithIds.fold(Applicative[F].unit)(todoRepository.insertTodos)
      } yield ()
    }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = todoListRepository.deleteTodoList(todoListId)

  override def getAllTodoLists(): F[GetAllTodoListsResponse] = todoListRepository.getAllTodoLists.map(GetAllTodoListsResponse(_))

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] = {
    for {
      todoList <- todoListRepository.getTodoList(id)
      todos    <- todoRepository.getTodos(id)
      finalTodoList = todoList.map(todoList => TodoList(todoList.todoListName, todoList.expiryDate, todos.map(todoDb => Todo(todoDb.name, todoDb.isCompleted))))
    } yield GetTodoListResponse(finalTodoList)
  }

  override def updateTodoList(id: TodoListId, todoList: TodoList): F[Unit] =
    for {
      _  <- todoListRepository.updateTodoList(
        TodoListDb(
          id,
          todoList.todoListName,
          todoList.expiryDate
        )
      )
      _ <- todoRepository.deleteTodos(id)
      todosWithIds = todoList.todos.map { todo =>
        val todoId = UUID.randomUUID()
        TodoDb(todoId, id, todo.name, todo.isCompleted)
      }.toNel
      _ <- todosWithIds.fold(Applicative[F].unit)(todoRepository.insertTodos)
    } yield ()
}