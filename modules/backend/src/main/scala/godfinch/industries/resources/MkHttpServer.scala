package godfinch.industries.resources

import cats.effect.{Async, Resource}
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger
import com.comcast.ip4s._

trait MkHttpServer[F[_]] {
  def newEmber(
                httpApp: HttpApp[F]
              ): Resource[F, Server]
}

object MkHttpServer {
  def apply[F[_]: MkHttpServer]: MkHttpServer[F] = implicitly
  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(
      s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}"
    )

  val thePort = port"8080"
  val theHost = host"0.0.0.0"

  implicit def forAsyncLogger[F[_]: Async: Logger]: MkHttpServer[F] =
    (httpApp: HttpApp[F]) => EmberServerBuilder.default[F]
      .withHost(theHost)
      .withPort(thePort)
      .withHttpApp(httpApp)
      .build
      .evalTap(showEmberBanner[F])
}