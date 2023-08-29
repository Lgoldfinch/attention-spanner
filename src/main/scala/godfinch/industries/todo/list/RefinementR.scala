package godfinch.industries.todo.list

import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.types.string.NonEmptyString
import godfinch.industries.attention.spanner.{EmailFormat, NonEmptyStringFormat, TodoListName}
import io.estatico.newtype.macros.newtype
import smithy4s.Refinement.PartiallyApplyRefinementProvider
import smithy4s._

package object RefinementR {
  type NameR = NonEmptyString

  object NameR extends RefinedTypeOps[NameR, String]

  @newtype
  final case class Name(str: NameR)

  object Name {
    def apply(str: String): Either[String, Name] = NonEmptyString.from(str).map(Name.apply)

    implicit val provider: RefinementProvider[NonEmptyStringFormat, String, Name] =
      Refinement.drivenBy[NonEmptyStringFormat](Surjection(apply, (e: Name) => e.str.value))
  }
}
//case class Email(value: String)
//object Email {
//
//  private def isValidEmail(value: String): Boolean = ???
//
//  def apply(value: String): Either[String, Email] =
//    if (isValidEmail(value)) Right(new Email(value))
//    else Left("Email is not valid")
//
//  implicit val provider = Refinement.drivenBy[EmailFormat](
//    Email.apply, // Tells smithy4s how to create an Email (or get an error message) given a string
//    (e: Email) => e.value // Tells smithy4s how to get a string from an Email
//  )
//}