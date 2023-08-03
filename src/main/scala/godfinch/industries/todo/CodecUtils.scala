package godfinch.industries.todo

import cats.implicits._
import skunk.Codec
import skunk.codec.all._
import skunk.data.Arr

import java.time.{Instant, LocalDateTime, ZoneOffset}

object CodecUtils {
  val instant: Codec[Instant] =
    timestamp.imap(_.toInstant(ZoneOffset.UTC))(LocalDateTime.ofInstant(_, ZoneOffset.UTC))

  implicit private class ArrayCodecConverterOps[A](a: Codec[Arr[A]]) {
    def toList: Codec[List[A]] = a.imap(_.toList)(i => Arr(i: _*))
  }
}

