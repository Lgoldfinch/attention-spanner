package godfinch.industries.repository

import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.attention.spanner._
import cats.implicits._

import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoListDb): F[Unit]

  def deleteTodoList(todoListId: TodoListId): F[Unit]

  def getAllTodoLists: F[List[TodoListDb]]

  def getTodoList(todoListId: TodoListId): F[Option[TodoListDb]]

  def updateTodoList(todoList: TodoListDb): F[Unit]
}

final class TodoRepositoryImpl[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
import TodoRepositoryImpl._

  override def insertTodoList(todoList: TodoListDb): F[Unit] = {
    todoList match {
      case TodoListDb(id, todoListName, created, todos) =>
        postgres.use(_.prepare(insertTodoListCommand).flatMap (
            _.execute(id *: todoListName *: created *: todos *: EmptyTuple).void
          )
    )
    }
  }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = postgres.use( session =>
      session.prepare(deleteTodoListCommand).flatMap {
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
  val insertTodoListCommand: Command[TodoListId *: TodoListName *: TimeCreated *: List[TodoName] *: EmptyTuple] = {
    sql"""
        insert into todos (id, name, created_timestamp, tasks)
        values ($todoListId, $todoListName, $timeCreated, $todoNames)
       """.command.contramap {
      case id *: todoName *: timeCreated *: todos *: EmptyTuple =>
        id *: todoName *: timeCreated *: todos *: EmptyTuple
    }
  }

  val deleteTodoListCommand: Command[TodoListId] =
    sql"""
        delete from todos where id = $todoListId
       """.command

  val getAllTodoListsQuery: Query[Void, TodoListDb] =
    sql"""
         select id, name, created_timestamp, tasks from todos
       """.query(todoListDecoder)

  val todoListDecoder: Decoder[TodoListDb] =  (todoListId *: todoListName *: timeCreated *: todoNames).to[TodoListDb]

  val getTodoListQuery: Query[TodoListId, TodoListDb] =
    sql"""
      select id, name, created_timestamp, tasks from todos
      where id = $todoListId
      """.query(todoListDecoder)

  val updateTodoListCommand: Command[TodoListDb] =
    sql"""
         UPDATE todos SET
           id = $todoListId,
           name = $todoListName,
           created_timestamp = $timeCreated,
           tasks = $todoNames
       """.command.to[TodoListDb]

//      .contramap(
//         update =>
//            update.todoListId *:
//            update.todoListName *:
//            update.timeCreated *:
//            update.todos *:
//
//    )
}
