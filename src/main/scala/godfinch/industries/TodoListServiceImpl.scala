package godfinch.industries

import godfinch.industries.hello._
import godfinch.industries.repository.TodoRepository
import smithy4s.Timestamp

import java.time.Instant
import java.util.UUID

final class TodoListServiceImpl[F[_]](todoRepository: TodoRepository[F]) extends HelloWorldService[F] {

  override def createTodoList(todoListName: TodoListName, createdTimestamp: TimeCreated, todos: List[TodoName]): F[Unit] =
    todoRepository
      .insertTodoList(
        TodoList(TodoListId(UUID.randomUUID()), todoListName, TimeCreated(Timestamp.fromInstant(Instant.now)), todos)
      )

  override def getAllTodoLists(): F[AllTodoListsB] = todoRepository.getAllTodoLists
}