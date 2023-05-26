package godfinch.industries

import cats.effect.IO
import com.example.hello.{Greeting, HelloWorldService, PersonName, Town}
import cats.implicits._

object HelloWorldImpl extends HelloWorldService[IO] {
  override def getTodos(): IO[Greeting] =
    Greeting("Fuckayou").pure[IO]
}