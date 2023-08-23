package godfinch.industries.todo.list

import cats.effect.IO
import godfinch.industries.attention.spanner.{ExpiryDate, TodoListDb, TodoListId, TodoListName}
import munit.ScalaCheckEffectSuite
import org.scalacheck.Gen
import org.scalacheck.effect.PropF
import smithy4s.Timestamp

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import java.util.Date

class TodoListRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {

  def newtypeGen[A, B](gen: Gen[A])(f: A => B): Gen[B] = gen.map(f)

  def localDateTimeGen: Gen[LocalDateTime] = {
    val currentDateTime = LocalDateTime.now()
    val minDateTime = currentDateTime.minusYears(1)
    val maxDateTime = currentDateTime.plusYears(1)

    val minEpochSeconds = minDateTime.toEpochSecond(ZoneOffset.UTC)
    val maxEpochSeconds = maxDateTime.toEpochSecond(ZoneOffset.UTC)

    val epochSecondsGen = Gen.chooseNum(minEpochSeconds, maxEpochSeconds)

    epochSecondsGen.map(epochSeconds =>
      LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
    )
  }

  val todoListIdGen: Gen[TodoListId] = newtypeGen(Gen.uuid)(TodoListId.apply)
  val todoListNameGen: Gen[TodoListName] = newtypeGen(Gen.alphaNumStr)(TodoListName.apply)
  val expiryDateGen: Gen[ExpiryDate] = newtypeGen(localDateTimeGen){localDateTime =>
    ExpiryDate(Timestamp.fromEpochSecond(localDateTime.toEpochSecond(ZoneOffset.UTC)))}

  val todoListGen: Gen[TodoListDb] =
    for {
      todoListId <- todoListIdGen
      todoListName <- todoListNameGen
      expiryDate <- expiryDateGen
    } yield TodoListDb(todoListId, todoListName, expiryDate)

  test("inserting a todo list") {
    PropF.forAllF(todoListGen) {
      todoList =>
        withPostgres {
          postgres =>
            val todoListRepository = new TodoListRepositoryImpl[IO](postgres)

            for {
              _ <- todoListRepository.insertTodoList(todoList)
              res <- todoListRepository.getTodoList(todoList.id)
            } yield assertEquals(res, Some(todoList))
        }
    }
    }
}
