package godfinch.industries

import cats.effect.{IO, Resource}
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.implicits._
import godfinch.industries.hello.HelloWorldService
import godfinch.industries.repository.TodoRepositoryImpl
import skunk.Session

object Routes {

//  private val example: Resource[IO, HttpRoutes[IO]] =
//    SimpleRestJsonBuilder.routes(new TodoListServiceImpl[IO](new )).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](HelloWorldService)

  def all(postgres: Resource[IO, Session[IO]]): Resource[IO, HttpRoutes[IO]] = {
    val todoRepository = new TodoRepositoryImpl[IO](postgres)
    val todoListService = new TodoListServiceImpl[IO](todoRepository)

    val example: Resource[IO, HttpRoutes[IO]] =
      SimpleRestJsonBuilder.routes(todoListService).resource

    example.map(_ <+> docs)
  }
}