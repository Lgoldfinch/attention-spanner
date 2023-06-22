package godfinch.industries.repository

import cats.Applicative
import cats.effect.Resource
import godfinch.industries.hello.{AllTodoListsB, TodoList, TodoListId, TodoListName, TodoName}
import cats.effect._
import cats.syntax.all._

import java.util.UUID
import cats.implicits._
import skunk._
import skunk.codec.all._
import skunk.data.{Arr, Type}
import skunk.implicits._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def getAllTodoLists: F[AllTodoListsB]

  def getTodoList(todoListId: TodoListId): F[TodoList]

  def updateTodoList(todoList: TodoList): F[Unit]
}

final class TodoRepositoryImpl[F[_]](postgres: Resource[F, Session[F]])(implicit A: Applicative[F]) extends TodoRepository[F] {

  val todoListName: Codec[TodoListName]         = text.imap[TodoListName](TodoListName(_))(_.value)
  val todoName: Codec[TodoName]         = text.imap[TodoName](TodoName(_))(_.value)
  val todoListId: Codec[TodoListId]         = uuid.imap[TodoListId](TodoListId(_))(_.value)
  val _todoName: Codec[Arr[TodoName]] =  Codec.array(_.value, str => Right((str)), Type._text)
  val todoNames: Codec[List[TodoName]] = _todoName.toList

  val todoListDecoder: Decoder[TodoList] =  (todoListId *: todoListName *: todoNames).to[TodoList]

  val getTodoListQuery: Query[TodoListName *: List[TodoName] *: TodoListId, TodoList] =
   sql"""
      select id, name, created_timestamp, tasks from todos
      """.query(todoListDecoder)

 val todoId = TodoListId(UUID.randomUUID())

  val todoName = TodoListName("Leaving for work")

  val todos = List(TodoName("Bring wallet"), TodoName("Bring laptop"), TodoName("Bring Keys"))

  override def insertTodoList(todoList: TodoList): F[Unit] = ???

  override def getAllTodoLists: F[AllTodoListsB] = {
    val todos = List(TodoName("Bring wallet"), TodoName("Bring laptop"), TodoName("Bring Keys"))


???
//    AllTodoListsB(
//    List(
//      TodoList(
//        todoName,
//        todos,
//        None,
//        )
//    )
//    ).pure[F]
  }

  override def getTodoList(todoListId: TodoListId): F[TodoList] = ???
//    TodoList(
//    todoName,
//    todos,
//    None
//  ).pure[F]

  override def updateTodoList(todoList: TodoList): F[Unit] = A.unit

  implicit private class ArrayCodecConverterOps[A](a: Codec[Arr[A]]) {
    def toList = a.imap(_.toList)(i => Arr(i: _*))
  }

}
