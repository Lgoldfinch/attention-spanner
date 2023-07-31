package godfinch.industries

import munit.FunSuite

import scala.annotation.tailrec

class Exercises extends FunSuite {
  sealed trait MyList[+A]
  case object Nil extends MyList[Nothing]
  case class Cons[+A](head: A, tail: MyList[A]) extends MyList[A]
  object MyList {
    def sum(ints: MyList[Int]): Int = ints match {
      case Nil => 0
      case Cons(x,xs) => x + sum(xs)
    }

    def product(ds: MyList[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x,xs) => x * product(xs)
    }

    def apply[A](as: A*): MyList[A] =
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))

    val example = Cons(1, Cons(2, Cons(3, Nil)))
    val example2 = MyList(1,2,3)
    val total = sum(example)
  }

  def tail[A](l: MyList[A]): MyList[A] =
    l match {
      case Nil => Nil
      case Cons(_, tail) => tail
    }

  @tailrec
  final def drop[A](n: Int, l: MyList[A]): MyList[A] = {
    if(n <= 0) l
    else
    l match {
      case Nil => Nil
      case Cons(_, tail) => drop(n - 1, tail)
    }
  }

  def dropWhile[A](l: MyList[A])(f: A => Boolean): MyList[A] =
    l match {
      case Nil => Nil
      case Cons(a, tail) => if(f(a)) dropWhile(tail)(f) else l
    }

  println(dropWhile(Cons(1, Cons(2, Cons(5, Nil))))(_ <= 2))
}
