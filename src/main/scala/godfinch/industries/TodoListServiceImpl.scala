package godfinch.industries

import cats.Functor
import godfinch.industries.hello._
import godfinch.industries.repository.TodoRepository
import smithy4s.Timestamp

import java.time.Instant
import java.util.UUID
import cats.implicits._

final class TodoListServiceImpl[F[_]: Functor](todoRepository: TodoRepository[F]) extends HelloWorldService[F] {

  override def createTodoList(todoListName: TodoListName, createdTimestamp: TimeCreated, todos: List[TodoName]): F[Unit] =
    todoRepository
      .insertTodoList(
        TodoList(TodoListId(UUID.randomUUID()), todoListName, TimeCreated(Timestamp.fromInstant(Instant.now)), todos)
      )

  override def getAllTodoLists(): F[AllTodoListsB] = todoRepository.getAllTodoLists

//  override def getTodoList(todoListId: TodoListId): F[Option[GetTodoListResponse]] = todoRepository.getTodoList(todoListId).map(_.map(GetTodoListResponse(_)))

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] = todoRepository.getTodoList(id).map(GetTodoListResponse(_))
}