package godfinch.industries.repository.model

import godfinch.industries.hello.{TimeCreated, TodoListId, TodoListName, TodoName}
import skunk.Codec
import skunk.codec.all.{text, timestamp, uuid}
import skunk.data.{Arr, Type}
import cats.implicits._
import smithy4s.Timestamp

import java.time.{Instant, LocalDateTime, ZoneOffset}

object Codecs {

  val instant: Codec[Timestamp] =
    timestamp.imap{aasd =>
      Timestamp.fromEpochSecond(aasd.toEpochSecond(ZoneOffset.UTC))
    }( asd => LocalDateTime.ofEpochSecond(asd.epochSecond, asd.nano, ZoneOffset.UTC))

  val timeCreated: Codec[TimeCreated] = instant.imap[TimeCreated](hello => TimeCreated(hello.toString))(timeCreated => Instant.parse(timeCreated.value))
  val todoListName: Codec[TodoListName] = text.imap[TodoListName](TodoListName(_))(_.value)
  val todoListId: Codec[TodoListId] = uuid.imap[TodoListId](TodoListId(_))(_.value)
  val _todoName: Codec[Arr[TodoName]] = Codec.array(_.value, str => Right(str), Type._text)
  val todoNames: Codec[List[TodoName]] = _todoName.toList

  implicit private class ArrayCodecConverterOps[A](a: Codec[Arr[A]]) {
    def toList: Codec[List[A]] = a.imap(_.toList)(i => Arr(i: _*))
  }
}
