package godfinch.industries.repository.model

import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.attention.spanner._
import cats.implicits._

import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

trait TodoListRepository[F[_]] {
  def insertTodoList(todoList: TodoListDb): F[Unit]

  def deleteTodoList(todoListId: TodoListId): F[Unit]

  def getAllTodoLists: F[List[TodoListDb]]

  def getTodoList(todoListId: TodoListId): F[Option[TodoListDb]]

  def updateTodoList(todoList: TodoListDb): F[Unit]
}

final class TodoListRepositoryImpl[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]) extends TodoListRepository[F] {
import TodoListRepositoryImpl._
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

private object TodoListRepositoryImpl {
  val todoListDbCodec: Codec[TodoListDb] =  (todoListId *: todoListName *: expiryDate *: todoNames).to[TodoListDb]

  val todoListEncoder: Encoder[TodoListDb] = (todoListId *: todoListName *: expiryDate *: todoNames).values.to[TodoListDb]

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
           created_timestamp = $expiryDate,
           tasks = $todoNames
       """.command.to[TodoListDb]
}
