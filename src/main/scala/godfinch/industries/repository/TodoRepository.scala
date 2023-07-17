package godfinch.industries.repository

import cats.Applicative
import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.attention.spanner._
import cats.implicits._

import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def deleteTodoList(todoListId: TodoListId): F[Unit]

  def getAllTodoLists: F[List[TodoList]]

  def getTodoList(todoListId: TodoListId): F[Option[TodoList]]

  def updateTodoList(todoList: TodoList): F[Unit]
}

final class TodoRepositoryImpl[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
import TodoRepositoryImpl._

  override def insertTodoList(todoList: TodoList): F[Unit] = {
    todoList match {
      case TodoList(id, todoListName, created, todos) =>
        postgres.use(_.prepare(insertTodoListCommand).flatMap (
            _.execute(id *: todoListName *: created *: todos *: EmptyTuple).void
          )
    )
    }
  }

  override def deleteTodoList(todoListId: TodoListId): F[Unit] = postgres.use( session =>
      session.prepare(deleteTodoListCommand).flatMap {
        _.execute(todoListId *: EmptyTuple).void
      }
  )

  override def getAllTodoLists: F[List[TodoList]] = {
    postgres.use(
      _.execute(getAllTodoListsQuery)
    )
  }

  override def getTodoList(todoListId: TodoListId): F[Option[TodoList]] =
    postgres.use { _.prepare(getTodoListQuery).flatMap {
        _.option(todoListId)
      }
    }

  override def updateTodoList(todoList: TodoList): F[Unit] = Applicative[F].unit
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

  val deleteTodoListCommand: Command[TodoListId *: EmptyTuple] = {
    sql"""
        delete from todos where id = $todoListId
       """.command.contramap {
      case id *: EmptyTuple =>
        id *: EmptyTuple
    }
  }

  val getAllTodoListsQuery: Query[Void, TodoList] =
    sql"""
         select id, name, created_timestamp, tasks from todos
       """.query(todoListDecoder)

  val todoListDecoder: Decoder[TodoList] =  (todoListId *: todoListName *: timeCreated *: todoNames).to[TodoList]

  val getTodoListQuery: Query[TodoListId, TodoList] =
    sql"""
      select id, name, created_timestamp, tasks from todos
      where id = $todoListId
      """.query(todoListDecoder)
}
