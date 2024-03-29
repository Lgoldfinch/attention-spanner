package godfinch.industries.todo.list

import cats.effect.{MonadCancelThrow, Resource}
import cats.implicits._
import godfinch.industries.attention.spanner._
import godfinch.industries.todo.list.TodoListCodecs._
import skunk._
import skunk.implicits._

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
  def getTodoListByName(name: TodoListName): F[Option[TodoListDb]] =
    postgres.use(_.prepare(getTodoListByNameQuery).flatMap(
      _.option(name)
    )
  )

  override def updateTodoList(todoList: TodoListDb): F[Unit] =
    postgres.use(_.prepare(updateTodoListCommand).flatMap(
      _.execute(todoList.name *: todoList.expiryDate *: todoList.id *: EmptyTuple).void
      )
    )
}

private object TodoListRepositoryImpl {
  val todoListDbCodec: Codec[TodoListDb] =  (todoListId *: todoListName *: expiryDate).to[TodoListDb]

  val todoListEncoder: Encoder[TodoListDb] = (todoListId *: todoListName *: expiryDate).values.to[TodoListDb]

  val insertTodoListCommand: Command[TodoListDb] = {
    sql"""
        INSERT INTO todo_list (id, name, expiry_date)
        VALUES $todoListEncoder
       """.command
  }

  val deleteTodoListCommand: Command[TodoListId] =
    sql"""
        DELETE FROM todo_list WHERE id = $todoListId
       """.command

  val getAllTodoListsQuery: Query[Void, TodoListDb] =
    sql"""
         SELECT id, name, expiry_date FROM todo_list
       """.query(todoListDbCodec)

  val getTodoListQuery: Query[TodoListId, TodoListDb] =
    sql"""
      SELECT id, name, expiry_date FROM todo_list
      WHERE id = $todoListId
      """.query(todoListDbCodec)

  val getTodoListByNameQuery: Query[TodoListName, TodoListDb] =
    sql"""
      SELECT id, name, expiry_date FROM todo_list
      WHERE name = $todoListName
      """.query(todoListDbCodec)

  val updateTodoListCommand: Command[TodoListName *: ExpiryDate *: TodoListId *: EmptyTuple] =
    sql"""
         UPDATE todo_list SET
           name = $todoListName,
           expiry_date = $expiryDate
         WHERE
            id = $todoListId
       """.command
}
