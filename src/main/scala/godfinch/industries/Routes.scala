package godfinch.industries

import cats.effect.{IO, Resource}
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.implicits._
import godfinch.industries.hello.HelloWorldService
import godfinch.industries.repository.TodoRepositoryImpl
import skunk.Session

object Routes {


  def all(postgres: Resource[IO, Session[IO]]): Resource[IO, HttpRoutes[IO]] = {
    val todoRepository = new TodoRepositoryImpl[IO](postgres)
    val todoListService: TodoListServiceImpl[IO] = new TodoListServiceImpl[IO](todoRepository)

    val example: Resource[IO, HttpRoutes[IO]] =
      SimpleRestJsonBuilder.routes(todoListService).resource

     val docs: HttpRoutes[IO] =
      smithy4s.http4s.swagger.docs[IO](HelloWorldService)

    example.map(_ <+> docs)
  }
}