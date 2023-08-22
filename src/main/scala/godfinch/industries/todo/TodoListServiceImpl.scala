package godfinch.industries.todo

import cats.implicits._
import cats.{Applicative, Monad}
import godfinch.industries.attention.spanner._
import godfinch.industries.todo.list.TodoListRepository
import godfinch.industries.todo.todos.TodoRepository
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

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] =
    for {
      todoListDb <- todoListRepository.getTodoList(id)
      _ <- todoListDb.traverse(todoListExpirationCheck)
      todos    <- todoRepository.getTodos(id)
      finalTodoList = todoListDb.map(todoList => TodoList(todoList.todoListName, todoList.expiryDate, todos.map(todoDb => Todo(todoDb.name, todoDb.isCompleted))))
    } yield GetTodoListResponse(finalTodoList)

  private def todoListExpirationCheck(todoList: TodoListDb): F[Unit] =
    if (Timestamp.nowUTC().isAfter(todoList.expiryDate.value))
      todoRepository.setCompletionStatus(
        IsCompleted(false),
        todoList.id
      )
    else
      Applicative[F].unit

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