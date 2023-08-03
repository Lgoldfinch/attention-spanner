package godfinch.industries.repository.model

import godfinch.industries.attention.spanner._
import skunk.Codec
import skunk.codec.all._
import skunk.data.{Arr, Type}
import cats.implicits._
import smithy4s.Timestamp

import java.time.{Instant, LocalDateTime, ZoneOffset}

object Codecs {
  val instant: Codec[Instant] =
  timestamp.imap(_.toInstant(ZoneOffset.UTC))(LocalDateTime.ofInstant(_, ZoneOffset.UTC))
  val expiryDate: Codec[ExpiryDate] = instant.imap[ExpiryDate](Timestamp.fromInstant)(_.value.toInstant)
  val todoListName: Codec[TodoListName] = text.imap[TodoListName](TodoListName(_))(_.value)
  val todoListId: Codec[TodoListId] = uuid.imap[TodoListId](TodoListId(_))(_.value)
  val _todoName: Codec[Arr[TodoName]] = Codec.array(_.value, str => Right(str), Type._text)

  val todoId: Codec[TodoId] = uuid.imap[TodoId](TodoId(_))(_.value)
  val todoName: Codec[TodoName] = text.imap[TodoName](TodoName(_))(_.value)
  val isTodoCompleted: Codec[IsCompleted] = bool.imap[IsCompleted](IsCompleted(_))(_.value)

  implicit private class ArrayCodecConverterOps[A](a: Codec[Arr[A]]) {
    def toList: Codec[List[A]] = a.imap(_.toList)(i => Arr(i: _*))
  }
}
