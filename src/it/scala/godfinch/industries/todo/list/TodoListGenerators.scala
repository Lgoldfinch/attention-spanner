package godfinch.industries.todo.list

import godfinch.industries.attention.spanner.{ExpiryDate, TodoListDb, TodoListId, TodoListName}
import godfinch.industries.utils.NonEmptyStringFormatR
import org.scalacheck.Gen
import smithy4s.Timestamp

import java.time.ZoneOffset
object TodoListGenerators {
  import godfinch.industries.utils.GeneralGenerators._

  val todoListIdGen: Gen[TodoListId] = newtypeGen(Gen.uuid)(TodoListId.apply)
  val todoListNameGen: Gen[TodoListName] = nonEmptyStringFormatGen(str => TodoListName(NonEmptyStringFormatR(str)))
  val expiryDateGen: Gen[ExpiryDate] = newtypeGen(localDateTimeGen) { localDateTime =>
    ExpiryDate(Timestamp.fromEpochSecond(localDateTime.toEpochSecond(ZoneOffset.UTC)))
  }

  val todoListGen: Gen[TodoListDb] =
    for {
      todoListId <- todoListIdGen
      todoListName <- todoListNameGen
      expiryDate <- expiryDateGen
    } yield TodoListDb(todoListId, todoListName, expiryDate)
}
