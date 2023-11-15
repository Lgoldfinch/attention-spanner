package godfinch.industries.utils

import eu.timepit.refined.types.string.NonEmptyString
import godfinch.industries.attention.spanner.NonEmptyStringFormat
import io.estatico.newtype.macros.newtype
import smithy4s.{Refinement, RefinementProvider}

object SmithyRefinements {
  @newtype
  final case class NonEmptyStringFormatR(str: NonEmptyString)

  object NonEmptyStringFormatR {
    def apply(str: String): Either[String, NonEmptyStringFormatR] = NonEmptyString.from(str).map(NonEmptyStringFormatR.apply)

    implicit val provider: RefinementProvider[NonEmptyStringFormat, String, NonEmptyStringFormatR] =
      Refinement.drivenBy[NonEmptyStringFormat](apply, _.str.value)
  }
}
