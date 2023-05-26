package godfinch.industries

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.ember.server._
import com.comcast.ip4s._

object Main extends IOApp.Simple {
  val run = Routes.all.flatMap { routes =>
    val thePort = port"9000"
    val theHost = host"localhost"
    EmberServerBuilder
      .default[IO]
      .withPort(thePort)
      .withHost(theHost)
      .withHttpApp(routes.orNotFound)
      .build <*
      Resource.eval(IO.println(s"Server started on: $theHost:$thePort"))
  }.useForever

}
