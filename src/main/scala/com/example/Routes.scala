package com.example

import cats.effect.{IO, Resource}
import com.example.hello.HelloWorldService
import org.http4s.HttpRoutes
import smithy4s.http4s.SimpleRestJsonBuilder
import cats.implicits._

object Routes {
  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(HelloWorldImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](HelloWorldService)

  val all: Resource[IO, HttpRoutes[IO]] = example.map(_ <+> docs)
}