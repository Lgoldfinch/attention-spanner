package godfinch.industries

import cats.effect.IO
import cats.effect.unsafe.implicits._
import godfinch.industries.Protocol.GetSuggestions
import org.http4s.Method._
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.client.dsl.io._
import org.http4s.dom.FetchClientBuilder

import scala.concurrent.Future

trait Api {
  def post(
          search: String,
          prefixOnly: Boolean = false
          ): Future[Either[Throwable, GetSuggestions.Response]]
}

object FutureApi extends Api {
  import org.scalajs.dom

  private val client: Client[IO] = FetchClientBuilder[IO].create

  private def ApiHost = {
    val scheme = dom.window.location.protocol
    val host = dom.window.location.host

    Uri.unsafeFromString(s"$scheme//$host")
  }

  override def post(search: String, prefixOnly: Boolean): Future[Either[Throwable, Protocol.GetSuggestions.Response]] = {
    client
      .expect[GetSuggestions.Response](
        POST(
          GetSuggestions.Request(search, Some(prefixOnly)),
          ApiHost / "get-suggestions"
        )
      )
      .attempt
      .unsafeToFuture()
  }

}
