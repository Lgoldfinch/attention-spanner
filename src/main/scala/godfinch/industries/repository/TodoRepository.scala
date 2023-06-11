package godfinch.industries.repository

import cats.Applicative
import godfinch.industries.hello.{TodoList, TodoListId, TodoListName, TodoName}

import java.util.UUID
import cats.implicits._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def getAllTodoLists: F[List[TodoList]]

  def getTodoList(todoListId: TodoListId): F[TodoList]

  def updateTodoList(todoList: TodoList): F[Unit]
}

final class TodoRepositoryImpl[F[_]]()(implicit A: Applicative[F]) extends TodoRepository[F] {
 val todoId = TodoListId(UUID.randomUUID())
  val todoName = TodoListName("Leaving for work")

  val todos = List(TodoName("Bring wallet"), TodoName("Bring laptop"), TodoName("Bring Keys"))

  override def insertTodoList(todoList: TodoList): F[Unit] = A.unit

  override def getAllTodoLists: F[List[TodoList]] = {
    val todos = List(TodoName("Bring wallet"), TodoName("Bring laptop"), TodoName("Bring Keys"))

    List(
      TodoList(
        todoId,
        todoName,
        todos)
    ).pure[F]
  }

  override def getTodoList(todoListId: TodoListId): F[TodoList] = TodoList(
    todoId, todoName, todos
  ).pure[F]

  override def updateTodoList(todoList: TodoList): F[Unit] = A.unit
}
