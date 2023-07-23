package godfinch.industries

import cats.{Functor, Monad}
import cats.effect.std.Console
import godfinch.industries.attention.spanner._
import godfinch.industries.repository.TodoRepository
import smithy4s.Timestamp

import java.time.Instant
import java.util.UUID
import cats.implicits._

final class TodoListServiceImpl[F[_]: Monad: Console](todoRepository: TodoRepository[F]) extends TodoListService[F] {

  override def createTodoList(todoListName: TodoListName, todos: List[TodoName]): F[Unit] = {
    val id = TodoListId(UUID.randomUUID())
    todoRepository
      .insertTodoList(
        TodoList(id, todoListName, TimeCreated(Timestamp.fromInstant(Instant.now)), todos)
      ) >> Console[F].println(id)
  }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = {
    todoRepository.deleteTodoList(todoListId)
  }

  override def getAllTodoLists(): F[GetAllTodoListsResponse] = todoRepository.getAllTodoLists.map(
    _.map { case TodoList(id, name, createdTimestamp, todos) => CreateTodoListRequest(todo) }
  )


  override def getTodoList(id: TodoListId): F[GetTodoListResponse] = todoRepository.getTodoList(id).map(GetTodoListResponse(_))
}