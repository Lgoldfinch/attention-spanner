package godfinch.industries.repository

import cats.Applicative
import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.hello._
import cats.implicits._

import java.util.UUID
import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

trait TodoRepository[F[_]] {
  def insertTodoList(todoList: TodoList): F[Unit]

  def getAllTodoLists: F[AllTodoListsB]

  def getTodoList(todoListId: TodoListId): F[Option[TodoList]]

  def updateTodoList(todoList: TodoList): F[Unit]
}

final class TodoRepositoryImpl[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
  val todoId = TodoListId(UUID.randomUUID())

import TodoRepositoryImpl._

  override def insertTodoList(todoList: TodoList): F[Unit] = {
    todoList match {
      case TodoList(id, todoListName, created, todos) =>
        postgres.use { session =>
          session.prepare(insertTodoListCommand).flatMap { cmd =>
            cmd.execute(id *: todoListName *: created *: todos *: EmptyTuple).void
          }
        }
    }
  }

  override def getAllTodoLists: F[AllTodoListsB] = {
    val todos = List(TodoName("Bring wallet"), TodoName("Bring laptop"), TodoName("Bring Keys"))
???
  }

  override def getTodoList(todoListId: TodoListId): F[Option[TodoList]] =
    postgres.use { _.prepare(getTodoListById).flatMap {
        _.option(todoListId)
      }
    }

  override def updateTodoList(todoList: TodoList): F[Unit] = Applicative[F].unit
}

object TodoRepositoryImpl {
  val insertTodoListCommand: Command[TodoListId *: TodoListName *: TimeCreated *: List[TodoName] *: EmptyTuple] = {
    sql"""
        insert into todos (id, name, created_timestamp, tasks)
        values ($todoListId, $todoListName, $timeCreated, $todoNames)
       """.command.contramap {
      case id *: todoName *: timeCreated *: todos *: EmptyTuple =>
        id *: todoName *: timeCreated *: todos *: EmptyTuple
    }
  }

  val todoListDecoder: Decoder[TodoList] =  (todoListId *: todoListName *: timeCreated *: todoNames).to[TodoList]

  val getTodoListById: Query[TodoListId, TodoList] =
    sql"""
      select id, name, created_timestamp, tasks from todos
      where id = $todoListId
      """.query(todoListDecoder)
}
