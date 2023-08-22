package godfinch.industries

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Chap5 extends App {

  def pair(i: => Int) = {
    (i, i)
  }

  pair {
    println("hi");
    1 + 41
  }

  // evaluates its arguments each time it references them is said to evaluate those arguments by name
  //

  import Stream._

  trait Stream[+A] {

    // 5.7 map, filter, append, flatmap using foldRight. Part of the exercise is
    // writing your own function signatures.

    def toList: List[A] = {
      @tailrec
      def go(stream: Stream[A], acc: List[A]): List[A] =
        stream match {
          case Cons(h, t) =>
            val res: List[A] = acc :+ h()
            go(t(), res)
          case Empty => acc
        }

      val res = go(this, List.empty[A])

      res.reverse
    }

    def take1(n: Int): List[A] = {
      toList.take(n)
    }

    def take2(n: Int): Stream[A] = {
      this match {
        case Cons(h, t) if n > 1 => Cons(h, () => t().take2(n - 1))
        case Cons(h, _) if n == 1 => Cons(h, () => Empty)
        case _ => Empty
      }
    }

    //    def takeWhile(p: A => Boolean): Stream[A] = {
    //          case Cons(h, t) if p(h()) => Cons(h, () => takeWhile(t()))
    //          case _ => Empty
    //        }


    def foldRight[B](z: => B)(f: (A, => B) => B): B = // The arrow `=>` in front of the argument type `B` means that the function `f` takes its second argument by name and may choose not to evaluate it.
      this match {
        case Cons(h, t) => f(h(), t().foldRight(z)(f)) // If `f` doesn't evaluate its second argument, the recursion never occurs.
        case _ => z
      }

    def forAll(p: A => Boolean): Boolean = foldRight(true) {
      (next, acc) =>
        p(next) && acc
    }

    def takeWhileViaFoldRight(p: A => Boolean): Stream[A] = {
      foldRight(Empty: Stream[A])((next, acc) =>
        if (p(next)) Cons(() => next, () => acc) else acc
      )
    }

    def map[B](f: A => B): Stream[B] =
      foldRight(Empty: Stream[B])(
        (next, acc) =>
          Cons(() => f(next), () => acc)
      )

    def filter(f: A => Boolean): Stream[A] =
      foldRight(Empty: Stream[A]) {
        (next, acc) =>
          if (f(next))
            Cons(() => next, () => acc)
          else acc
      }

    def append[B >: A](s: => Stream[B]): Stream[B] =
      foldRight(s)((h, t) =>
        cons(h, t)
      )

    def flatMap[B](f: A => Stream[B]): Stream[B] = foldRight(Empty: Stream[B])(
      (h, t) => f(h) append t
    )
  }

  println(List(1, 23, 3).filter(_ < 2))
  println(Cons(() => 1, () => Cons(() => 2000, () => Cons(() => 451, () => Empty))).flatMap(a => Cons(() => a + 1, () => Empty)).toList)


  case object Empty extends Stream[Nothing]

  case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

  object Stream {
    def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
      lazy val head = hd
      lazy val tail = tl
      Cons(() => head, () => tail)
    }

    def empty[A]: Stream[A] = Empty

    def apply[A](as: A*): Stream[A] =
      if (as.isEmpty) empty
      else cons(as.head, apply(as.tail: _*))

    val ones: Stream[Int] = cons(1, ones)

    def constant[A](a: A): Stream[A] = {
      lazy val tail: Stream[A] = Cons(() => a, () => tail) // Why?

      Cons(() => a, () => tail)
    }

    def from(n: Int): Stream[Int] = {
      //      lazy val tail: Stream[Int] = Cons(() => n, () => )
      Cons(() => n, () => from(n + 1))
    }

    def fibs: Stream[Int] = {
      def go(current: Int, next: Int): Stream[Int] = {
        Cons(
          () => current,
          () => go(next, current + next)
        )
      }

      go(0, 1)
    }

    def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] =
      f(z) match {
        case Some((a, s)) => Cons(() => a, () => unfold(s)(f))
        case None => Empty
      }
  }



}