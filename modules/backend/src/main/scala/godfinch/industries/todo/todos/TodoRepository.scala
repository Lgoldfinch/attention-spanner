package godfinch.industries.todo.todos

import cats.data.NonEmptyList
import cats.effect.Resource
import cats.effect.kernel.Concurrent
import cats.implicits._
import godfinch.industries.attention.spanner._
import skunk._
import skunk.implicits._
import TodoCodecs.{isTodoCompleted, _}
import godfinch.industries.todo.list.TodoListCodecs.todoListId

trait TodoRepository[F[_]] {
  def insertTodos(todoList: NonEmptyList[TodoDb]): F[Unit]

  def deleteTodos(todoListId: TodoListId): F[Unit]

  def getTodos(todoListId: TodoListId): F[List[TodoDb]]

  def setCompletionStatus(isCompleted: IsCompleted, todoListId: TodoListId): F[Unit]
}

final class TodoRepositoryImpl[F[_]: Concurrent](postgres: Resource[F, Session[F]]) extends TodoRepository[F] {
import TodoRepositoryImpl._
  override def insertTodos(todoLists: NonEmptyList[TodoDb]): F[Unit] =
  postgres.use(
          _.prepare(insertTodoListCommand).flatMap ( a =>
            todoLists.traverse( todo =>
            a.execute(todo)
          ).void
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

  def setCompletionStatus(isCompleted: IsCompleted, todoListId: TodoListId): F[Unit] = postgres.use(
    _.prepare(setCompletionStatusQuery).flatMap(_.execute(
      isCompleted *: todoListId *: EmptyTuple)
    ).void
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
         DELETE FROM todo WHERE todo_list_id = $todoListId
        """.command


  val getTodosQuery: Query[TodoListId, TodoDb] =
    sql"""
      SELECT id, todo_list_id, name, is_completed FROM todo
      WHERE todo_list_id = $todoListId
      """.query(todoDbCodec)

  val setCompletionStatusQuery: Command[IsCompleted *: TodoListId *: EmptyTuple] =
    sql"""
        UPDATE todo SET
          is_completed = $isTodoCompleted
          where todo_list_id = $todoListId
       """.command
}
