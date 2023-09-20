package godfinch.industries.todo.list

import godfinch.industries.attention.spanner.{ExpiryDate, TodoListId, TodoListName}
import godfinch.industries.todo.CodecUtils.instant
import godfinch.industries.utils.SmithyRefinements.NonEmptyStringFormatR
import skunk.Codec
import smithy4s.Timestamp
import skunk.codec.all._

object TodoListCodecs {
  val expiryDate: Codec[ExpiryDate] = instant.imap[ExpiryDate](Timestamp.fromInstant)(_.value.toInstant)
  val todoListName: Codec[TodoListName] =
    text.eimap[TodoListName](NonEmptyStringFormatR.apply(_).map(TodoListName(_)))(_.value.str.value)

  val todoListId: Codec[TodoListId] = uuid.imap[TodoListId](TodoListId(_))(_.value)
}
