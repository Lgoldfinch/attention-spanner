package godfinch.industries.repository

import cats.Applicative
import cats.effect.Resource
import godfinch.industries.hello._
import java.util.UUID
import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._
import cats.implicits._
import cats.effect._
import cats.syntax.all._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def getAllTodoLists: F[AllTodoListsB]

  def getTodoList(todoListId: TodoListId): F[TodoList]

  def updateTodoList(todoList: TodoList): F[Unit]
}

final class TodoRepositoryImpl[F[_]](postgres: Resource[F, Session[F]])(implicit A: Applicative[F]) extends TodoRepository[F] {
  val todoListDecoder: Decoder[TodoList] =  (todoListId *: todoListName *: timeCreated *: todoNames).to[TodoList]

  val getTodoListById =
   sql"""
      select id, name, created_timestamp, tasks from todos
      where id = $todoListId
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

  override def getTodoList(todoListId: TodoListId): F[TodoList] = {
    ???
//     postgres.use{ session =>

//       val helo = session.prepare(getTodoListByTodoListId)
//         .flatMap {
//         q =>
//       }
//     }
  }
//    TodoList(
//    todoName,
//    todos,
//    None
//  ).pure[F]

  override def updateTodoList(todoList: TodoList): F[Unit] = A.unit
}
