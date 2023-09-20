package godfinch.industries.todo

import cats.implicits._
import cats.{Applicative, Monad}
import godfinch.industries.attention.spanner._
import godfinch.industries.todo.list.TodoListRepository
import godfinch.industries.todo.todos.TodoRepository
import godfinch.industries.utils.uuid.{GenUUID, ID}
import smithy4s.Timestamp

final class TodoListServiceImpl[F[_]: GenUUID: Monad](todoRepository: TodoRepository[F], todoListRepository: TodoListRepository[F]) extends TodoListService[F] {

  private def enrichTodo(todo: Todo, todoListId: TodoListId): F[TodoDb] = {
    ID.make[F, TodoId].map {
      id => TodoDb(id, todoListId, todo.name, todo.isCompleted)
    }
  }

  override def createTodoList(todoListName: TodoListName, expiryDate: ExpiryDate, todos: List[Todo]): F[Unit] = {
      for {
        todoListId <- ID.make[F, TodoListId]
        _ <- todoListRepository.insertTodoList(TodoListDb(todoListId, todoListName, expiryDate))
        todosWithIds <- todos.traverse(enrichTodo(_, todoListId))
        _ <- todosWithIds.toNel.fold(Applicative[F].unit)(todoRepository.insertTodos)
      } yield ()
    }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = todoListRepository.deleteTodoList(todoListId)

  override def getAllTodoLists(): F[GetAllTodoListsResponse] = todoListRepository.getAllTodoLists.map(GetAllTodoListsResponse(_))

  private def todoListExpirationCheck(todoList: TodoListDb): F[Unit] =
    if (Timestamp.nowUTC().isAfter(todoList.expiryDate.value))
      todoRepository.setCompletionStatus(
        IsCompleted(false),
        todoList.id
      )
    else
      Applicative[F].unit

  override def getTodoList(id: TodoListId): F[GetTodoListResponse] =
    for {
      todoListDb <- todoListRepository.getTodoList(id)
      _ <- todoListDb.traverse(todoListExpirationCheck)
      todos    <- todoRepository.getTodos(id)
      finalTodoList = todoListDb.map(todoList => TodoList(todoList.todoListName, todoList.expiryDate, todos.map(todoDb => Todo(todoDb.name, todoDb.isCompleted))))
    } yield GetTodoListResponse(finalTodoList)

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
      todosWithIds <- todoList.todos.traverse(enrichTodo(_, id))
      _ <- todosWithIds.toNel.fold(Applicative[F].unit)(todoRepository.insertTodos)
    } yield ()


}