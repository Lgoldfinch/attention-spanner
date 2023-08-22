package godfinch.industries

import munit.FunSuite

import scala.+:

class Chap4 extends FunSuite {

  import scala.{Option => _, Some => _, Either => _, _} // hide std library `Option`, `Some` and `Either`, since we are writing our own in this chapter

  trait Option[+A] {
    def map[B](f: A => B): Option[B] = this match {
      case None => None
      case Some(get) => Some(f(get))
    }

    def flatMap[B](f: A => Option[B]): Option[B] = map(f) getOrElse None

    def getOrElse[B >: A](default: => B): B = this match {
      case None => default
      case Some(get) => get
    }

    def orElse[B >: A](ob: => Option[B]): Option[B] = this map (Some(_)) getOrElse ob


    def filter(f: A => Boolean): Option[A] = flatMap(a =>
      if (f(a)) Some(a)
      else None
    )
  }

  case class Some[+A](get: A) extends Option[A]

  case object None extends Option[Nothing]

  def mean(xs: Seq[Double]): Option[Double] = {
    if (xs.isEmpty)
      None
    else Some(xs.sum / xs.length)
  }

  def variance(xs: Seq[Double]): Option[Double] = {
    mean(xs) flatMap (
      m =>
        mean(xs.map(x => math.pow(x - m, 2)))
      )
  }

  def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] =
    a
      .flatMap(hello =>
        b
          .map(bye => f(hello, bye))
      )

  def sequence[A](a: List[Option[A]]): Option[List[A]] = {
    a.foldRight[Option[List[A]]](Some(Nil))((next, acc) =>
      next.flatMap(
        (n: A) =>
          acc.map(ac => n :: ac)
      )
    )
  }

  def sequence_1[A](a: List[Option[A]]): Option[List[A]] =
    a.foldRight[Option[List[A]]](Some(Nil))((x, y) =>
      x
        .flatMap(hello =>
          y
            .map(bye => hello :: bye)
        )
    )

  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
    a.foldRight[Option[List[B]]](Some(Nil))((next, acc) =>
      f(next).flatMap(
        b => acc.map(
          l => l :+ b
        )
      )
    )
  }

  trait Either[+E, +A] {
    def map[B](f: A => B): Either[E, B] = this match {
      case Right(value) => Right(f(value))
      case Left(value) => Left(value)
    }

    def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
      case Left(value) => Left(value)
      case Right(value) => f(value)
    }

    def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
      case Right(value) => Right(value)
      case _ => b
    }

    def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = {
      for {
        a <- this
        b2 <- b
      } yield f(a, b2)
    }
  }

  case class Left[+E](value: E) extends Either[E, Nothing]

  case class Right[+A](value: A) extends Either[Nothing, A]

  def sequence[E, A](a: List[Either[E, A]]): Either[E, List[A]] = {
    a.foldRight(Right(Nil): Either[E, List[A]])((next, acc) =>
      next.flatMap(
        (n: A) =>
          acc.map(ac => n :: ac)
      )
    )
  }
}
