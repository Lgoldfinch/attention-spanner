package godfinch.industries.todo.list

import godfinch.industries.attention.spanner.{ExpiryDate, TodoListId, TodoListName}
import godfinch.industries.todo.CodecUtils.instant
import skunk.Codec
import smithy4s.Timestamp
import skunk.codec.all._

object TodoListCodecs {
  val expiryDate: Codec[ExpiryDate] = instant.imap[ExpiryDate](Timestamp.fromInstant)(_.value.toInstant)
  val todoListName: Codec[TodoListName] = text.imap[TodoListName](TodoListName(_))(_.value)
  val todoListId: Codec[TodoListId] = uuid.imap[TodoListId](TodoListId(_))(_.value)
}
