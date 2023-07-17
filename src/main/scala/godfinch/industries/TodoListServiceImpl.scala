package godfinch.industries

import cats.{Functor, Monad}
import cats.effect.std.Console
import godfinch.industries.hello._
import godfinch.industries.repository.TodoRepository
import smithy4s.Timestamp

import java.time.Instant
import java.util.UUID
import cats.implicits._

final class TodoListServiceImpl[F[_]: Monad: Console](todoRepository: TodoRepository[F]) extends HelloWorldService[F] {

  override def createTodoList(todoListName: TodoListName, todos: List[TodoName]): F[Unit] = {
    val id = TodoListId(UUID.randomUUID())
    todoRepository
      .insertTodoList(
        TodoList(id, todoListName, TimeCreated(Timestamp.fromInstant(Instant.now)), todos)
      ) >> Console[F].println(id)
  }

  override def getAllTodoLists(): F[AllTodoListsB] = todoRepository.getAllTodoLists

//  override def getTodoList(todoListId: TodoListId): F[Option[GetTodoListResponse]] = todoRepository.getTodoList(todoListId).map(_.map(GetTodoListResponse(_)))

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] = todoRepository.getTodoList(id).map(GetTodoListResponse(_))
}