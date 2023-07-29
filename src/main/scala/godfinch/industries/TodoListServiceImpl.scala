package godfinch.industries

import cats.Monad
import cats.effect.kernel.Clock
import cats.effect.std.Console
import cats.implicits._
import godfinch.industries.attention.spanner._
import godfinch.industries.repository.TodoRepository
import smithy4s.Timestamp

import java.util.UUID

final class TodoListServiceImpl[F[_]: Monad: Console: Clock](todoRepository: TodoRepository[F]) extends TodoListService[F] {

  override def createTodoList(todoListName: TodoListName, todos: List[TodoName]): F[Unit] = {
    val id = TodoListId(UUID.randomUUID())
    for {
      now <- Clock[F].realTimeInstant
      _   <- todoRepository.insertTodoList(TodoListDb(id, todoListName, TimeCreated(Timestamp.fromInstant(now)), todos))
      _   <- Console[F].println(id)
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
          TimeCreated(Timestamp.fromInstant(now)),
          todoList.todos)
      )
    } yield ()
  }
}