package godfinch.industries.todo.todos

import godfinch.industries.attention.spanner.{IsCompleted, Todo, TodoDb, TodoId, TodoName}
import godfinch.industries.todo.list.TodoListGenerators.todoListIdGen
import godfinch.industries.utils.SmithyRefinements.NonEmptyStringFormatR
import org.scalacheck.Gen

object TodoGenerators {
  import godfinch.industries.utils.GeneralGenerators._

  val todoIdGen: Gen[TodoId] = newtypeGen(Gen.uuid)(TodoId.apply)
  val todoNameGen: Gen[TodoName] = nonEmptyStringFormatGen(str =>
    TodoName(NonEmptyStringFormatR(str))
  )

  val isCompleted: Gen[IsCompleted] = newtypeGen(Gen.oneOf(true, false))(IsCompleted.apply)

  val todoGen: Gen[Todo] = for {
    name <- todoNameGen
    isCompleted <- isCompleted
  } yield Todo(name, isCompleted)

  val todoDbGen: Gen[TodoDb] = for {
            id <- todoIdGen
    todoListId <- todoListIdGen
    name <- todoNameGen
    isCompleted <- isCompleted
  } yield TodoDb(id, todoListId, name, isCompleted)

}
