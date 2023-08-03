package godfinch.industries.todo.todos

import godfinch.industries.attention.spanner._
import skunk.Codec
import skunk.codec.all._

object TodoCodecs {
  val todoId: Codec[TodoId] = uuid.imap[TodoId](TodoId(_))(_.value)
  val todoName: Codec[TodoName] = text.imap[TodoName](TodoName(_))(_.value)
  val isTodoCompleted: Codec[IsCompleted] = bool.imap[IsCompleted](IsCompleted(_))(_.value)
}
