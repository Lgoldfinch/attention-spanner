package godfinch.industries.todo.todos

import godfinch.industries.TestPostgresContainer
import munit.ScalaCheckEffectSuite
import org.scalacheck.effect.PropF

class TodoRepositorySpec extends TestPostgresContainer with ScalaCheckEffectSuite {
  import TodoGenerators._

  test("inserts and retrieving todos") {
      PropF.forAllF(todoListGen)
  }

}
