package godfinch.industries

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object Protocol {
  object GetSuggestions {
        case class Request(
                            search: String,
                            prefixOnly: Option[Boolean] = None
                          )

    object Request {
      implicit val decoderRequest: Decoder[Request] = deriveDecoder
      implicit val encoderRequest: Encoder[Request] = deriveEncoder

    }


    case class Response(suggestions: List[String])

    object Response {
      implicit val decoderResponse: Decoder[Response] = deriveDecoder
    }
  }
}
