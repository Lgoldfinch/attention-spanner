package godfinch.industries.repository

import cats.data.NonEmptyList
import cats.effect.kernel.Concurrent
import cats.effect.{MonadCancelThrow, Resource}
import godfinch.industries.attention.spanner._
import godfinch.industries.attention.spanner._
import cats.implicits._
import skunk._
import skunk.implicits._
import godfinch.industries.repository.model.Codecs._

trait TodoRepository[F[_]] {
  def insertTodoLists(todoList: NonEmptyList[TodoDb]): F[Unit]

  def deleteTodos(todoListId: TodoListId): F[Unit]

  def getTodos(todoListId: TodoListId): F[List[TodoDb]]
}

final class TodoRepositoryImpl[F[_]: Concurrent](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
import TodoRepositoryImpl._
  override def insertTodoLists(todoLists: NonEmptyList[TodoDb]): F[Unit] =
  postgres.use(
          _.prepare(insertTodoListCommand).flatMap (
            _.execute(todoLists).void
          )
      )


  override def deleteTodos(todoListId: TodoListId): F[Unit] = postgres.use(
    _.prepare(deleteTodosCommand).flatMap {
        _.execute(todoListId).void
      }
  )

  override def getTodos(todoListId: TodoListId): F[List[TodoDb]] =
    postgres.use(_.prepare(getTodosQuery).flatMap(
      _.stream(todoListId, 1024).compile.toList
    )
  )
}

private object TodoRepositoryImpl {
  val todoDbCodec: Codec[TodoDb] =  (todoId *: todoListId *: todoName *: isTodoCompleted).to[TodoDb]

  val todoListEncoder: Encoder[TodoDb] = (todoId *:  todoListId *: todoName *: isTodoCompleted).values.to[TodoDb]

  val insertTodoListCommand: Command[TodoDb] = {
    sql"""
        INSERT INTO todo (id, todo_list_id, name, is_completed)
        VALUES $todoListEncoder
       """.command
  }

  val deleteTodosCommand: Command[TodoListId] =
    sql"""
         DELETE FROM todo WHERE id = $todoListId
        """.command


  val getTodosQuery: Query[TodoListId, TodoDb] =
    sql"""
      SELECT id, todo_list_id, name, is_completed FROM todo
      WHERE id = $todoListId
      """.query(todoDbCodec)

}
