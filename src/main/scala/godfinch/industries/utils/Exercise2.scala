package godfinch.industries.utils

import cats.effect.{IO, IOApp}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import cats.implicits._

import scala.util.Random

object  Exercise2 extends IOApp.Simple {

  def printHello(client: Client[IO]): IO[Unit] = {
    val random = new Random
    val range = List.range(0, 100)

    val elementThatWillError: Int = random.nextInt(range.length)

    range.parTraverse_ { n =>

      (if (n == elementThatWillError) {
        IO.canceled
      }
      else {
        IO.println(s"Step $n") >>
          client
            .expect[String]("https://http4s.org/v0.23/docs/client.html")
            .flatTap(IO.println)
        })
        .onCancel(IO.println(s"Step $n cancelled because Step $elementThatWillError failed"))
    }
  }

  def printHello2(client: Client[IO]): IO[Unit] = {
    val random = new Random
    val range = List.range(0, 100)

    val elementThatWillError: Int = random.nextInt(range.length)

    range.parTraverse_ { n =>

      (if (n == elementThatWillError) {
        IO.canceled
      }
      else {

        IO.println(s"Step: $n") >>
          client
            .expect[String]("https://http4s.org/v0.23/docs/client.html")
            .flatTap(res => IO.println(s"Step $n: $res")
        )
      }
    )
        .onCancel(IO.println(s"Step $n cancelled because Step $elementThatWillError failed"))
    }
  }

  override def run: IO[Unit] = EmberClientBuilder.default[IO].build.use(printHello2)
}
