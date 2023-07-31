package godfinch.industries

import cats.effect.{IO, Resource}
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.implicits._
import godfinch.industries.attention.spanner.TodoListService
import godfinch.industries.repository.TodoRepositoryImpl
import godfinch.industries.repository.model.TodoListRepositoryImpl
import skunk.Session

object Routes {


  def all(postgres: Resource[IO, Session[IO]]): Resource[IO, HttpRoutes[IO]] = {
    val todoRepository = new TodoRepositoryImpl[IO](postgres)
    val todoListRepository = new TodoListRepositoryImpl[IO](postgres)
    val todoListService: TodoListServiceImpl[IO] = new TodoListServiceImpl[IO](todoRepository, todoListRepository)

    val todoRoutes: Resource[IO, HttpRoutes[IO]] =
      SimpleRestJsonBuilder.routes(todoListService).resource

     val docs: HttpRoutes[IO] =
      smithy4s.http4s.swagger.docs[IO](TodoListService)

    todoRoutes.map(_ <+> docs)
  }
}