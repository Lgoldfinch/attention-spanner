package godfinch.industries

import scala.concurrent.Future

trait Api {
  def post(
          search: String,
          prefixOnly: Boolean = false
          ): Future[Either[Throwable, String]]
}

object FutureApi extends Api {
  override def post(search: String, prefixOnly: Boolean): Future[Either[Throwable, String]] = ???
}
