package godfinch.industries

import cats.effect.{IO, Resource}
import weaver.IOSuite
import weaver.scalacheck.{CheckConfig, Checkers}
import cats.implicits._

abstract class ResourceSuite extends IOSuite with Checkers {
  override def checkConfig: CheckConfig = CheckConfig.default.copy(minimumSuccessful = 1)

  implicit class SharedResOps(res: Resource[IO, Res]) {
    def beforeAll(f: Res => IO[Unit]): Resource[IO, Res] = res.evalTap(f)

    def afterAll(f: Res => IO[Unit]): Resource[IO, Res] =
      res.flatTap(x => Resource.make(IO.unit)(_ => f(x)))
  }
}
