package godfinch.industries

import godfinch.industries.hello._
import godfinch.industries.repository.TodoRepository

import java.util.UUID

final class TodoListService[F[_]](todoRepository: TodoRepository[F]) extends HelloWorldService[F] {

  override def createTodoList(todoListName: TodoListName, todos: List[TodoName], todoListId: Option[TodoListId]): F[Unit] =
    todoRepository
      .insertTodoList(
        TodoList(todoListName, todos, Some(TodoListId(UUID.randomUUID())))
      )

  override def getAllTodoLists(): F[AllTodoListsB] = todoRepository.getAllTodoLists
}