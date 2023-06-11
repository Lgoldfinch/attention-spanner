package godfinch.industries.repository

import godfinch.industries.hello.{TodoList, TodoListId}

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def getAllTodoLists: F[List[TodoList]]

  def getTodoList(todoListId: TodoListId): F[TodoList]

  def updateTodoList(todoList: TodoList): F[Unit]
}
