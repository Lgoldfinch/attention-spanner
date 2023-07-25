package godfinch.industries.repository

import cats.data.NonEmptyList
import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.attention.spanner._
import cats.implicits._
import eu.timepit.refined.collection.NonEmpty
import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

// Get all for a given todo list id.
// insert many
// update
//
trait TodoRepository[F[_]] {
  def insertTodoList(todoList: NonEmptyList[Todo]): F[Unit]

  def deleteTodoList(todoListId: TodoListId): F[Unit]

  def getAllTodoLists: F[List[TodoListDb]]

  def getTodoList(todoListId: TodoListId): F[Option[TodoListDb]]

  def updateTodoList(todoList: TodoListDb): F[Unit]
}

final class TodoRepositoryImpl[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
import TodoRepositoryImpl._
  override def insertTodoList(todoList: TodoListDb): F[Unit] =
        postgres.use(
          _.prepare(insertTodoListCommand).flatMap (
            _.execute(todoList).void
          )
      )


  override def deleteTodoList(todoListId: TodoListId): F[Unit] = postgres.use(
    _.prepare(deleteTodoListCommand).flatMap {
        _.execute(todoListId).void
      }
  )

  override def getAllTodoLists: F[List[TodoListDb]] = {
    postgres.use(
      _.execute(getAllTodoListsQuery)
    )
  }

  override def getTodoList(todoListId: TodoListId): F[Option[TodoListDb]] =
    postgres.use(_.prepare(getTodoListQuery).flatMap(
      _.option(todoListId)
    )
  )

  override def updateTodoList(todoList: TodoListDb): F[Unit] =
    postgres.use(_.prepare(updateTodoListCommand).flatMap(
      _.execute(todoList).void
      )
    )
}

private object TodoRepositoryImpl {
  val todoListDbCodec: Codec[TodoListDb] =  (todoListId *: todoListName *: timeCreated *: todoNames).to[TodoListDb]

  val todoListEncoder: Encoder[TodoListDb] = (todoListId *: todoListName *: timeCreated *: todoNames).values.to[TodoListDb]

  val insertTodoListCommand: Command[TodoListDb] = {
    sql"""
        INSERT INTO todos (id, name, created_timestamp, tasks)
        VALUES $todoListEncoder
       """.command
  }

  val deleteTodoListCommand: Command[TodoListId] =
    sql"""
        DELETE FROM todos WHERE id = $todoListId
       """.command

  val getAllTodoListsQuery: Query[Void, TodoListDb] =
    sql"""
         SELECT id, name, created_timestamp, tasks FROM todos
       """.query(todoListDbCodec)

  val getTodoListQuery: Query[TodoListId, TodoListDb] =
    sql"""
      SELECT id, name, created_timestamp, tasks FROM todos
      WHERE id = $todoListId
      """.query(todoListDbCodec)

  val updateTodoListCommand: Command[TodoListDb] =
    sql"""
         UPDATE todos SET
           id = $todoListId,
           name = $todoListName,
           created_timestamp = $timeCreated,
           tasks = $todoNames
       """.command.to[TodoListDb]
}
