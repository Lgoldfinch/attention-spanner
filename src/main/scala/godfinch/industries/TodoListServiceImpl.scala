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

final class TodoListServiceImpl[F[_]: Monad: Console: Clock](todoRepository: TodoRepository[F], todoListRepository: TodoListRepository[F]) extends TodoListService[F] {

  override def createTodoList(todoListName: TodoListName, expiryDate: ExpiryDate, todos: List[Todo]): F[Unit] = {
      val todoListId = TodoListId(UUID.randomUUID())
      for {
        _ <- todoListRepository.insertTodoList(TodoListDb(todoListId, todoListName, expiryDate))
        todosWithIds = todos.map{ todo =>
          val todoId = UUID.randomUUID()
          TodoDb(todoId, todoListId, todo.name, todo.isCompleted)
        }.toNel
        _ <- todosWithIds.fold(Applicative[F].unit)(todoRepository.insertTodoLists)
        _ <- Console[F].print(todoListId)
      } yield ()
    }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = {
    todoRepository.deleteTodoList(todoListId)
  }

  override def getAllTodoLists(): F[GetAllTodoListsResponse] = todoRepository.getAllTodoLists.map(GetAllTodoListsResponse(_))

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] = todoRepository.getTodoList(id).map(GetTodoListResponse(_))

  override def updateTodoList(id: TodoListId, todoList: TodoList): F[Unit] = {
    for {
      now <- Clock[F].realTimeInstant
      _   <- todoRepository.updateTodoList(
        TodoListDb(
          id,
          todoList.todoListName,
          ExpiryDate(Timestamp.fromInstant(now)),
          todoList.todos)
      )
    } yield ()
  }
}