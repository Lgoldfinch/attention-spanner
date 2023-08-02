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

    def foldRight[A, B](l: MyList[A], z: B)(f: (A, B) => B): B =
      l match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(xs, z)(f))
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

  def dropWhile[A](l: MyList[A])(f: A => Boolean): MyList[A] = l match {
      case Cons(a, tail)  if(f(a))=>  dropWhile(tail)(f)
      case _ => Nil
    }

  println(dropWhile(Cons(1, Cons(2, Cons(5, Nil))))(_ <= 2))

  def setHead[A](x: A, l: MyList[A]): MyList[A] = l match {
      case Nil => Cons(x, Nil)
      case Cons(_, tail) => Cons(x, tail)
    }

  def init[A](l: MyList[A]): MyList[A] = l match {
    case Nil => throw new Exception("no")
    case a @ Cons(_, Nil) => a
    case Cons(head, tail) => Cons(head, init(tail))
  }

  def foldRight[A, B](l: MyList[A], z: B)(f: (A, B) => B): B =
    l match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def length[A](l: MyList[A]): Int = foldRight(l, 0)((_, acc) =>
      acc + 1
  )

@tailrec
  final def foldLeft[A, B](l: MyList[A], z: B)(f: (B, A) => B): B = { ////////
    l match {
      case Nil => z
      case Cons(h, t) =>
        foldLeft(t, f(z, h))(f)

    }
  }

  def reverse[A](l: MyList[A]): MyList[A] = foldLeft(l, Nil: MyList[A])((acc, head) => /////////
    Cons(head, acc)
  )

  println(foldLeft(Cons(1,Cons(2, Cons(3, Nil))), 0)(_ + _))

  def sumViaFoldLeft(ints: MyList[Int]): Int = foldLeft(ints, 0)(_ + _)

  def productViaFoldLeft(ints: MyList[Double]): Double = foldLeft(ints, 0.0)(_ * _)

  def foldRightAsFoldLeft[A, B](l: MyList[A], z: B)(f: (A, B) => B) = foldLeft(l, z)((next, acc) =>
      f(acc, next)
  )

  def append[A](a1: MyList[A], a2: MyList[A]): MyList[A] = foldLeft(a1, a2)((list2, head) =>
      Cons(head, list2)
  )

println(append(Cons(5, Cons(234, Nil)), Cons(1, Nil)))

  def concat[A](l: MyList[MyList[A]]): MyList[A] = foldRightAsFoldLeft(l, Nil: MyList[A])(append) ////////////////

  def add1(ints: MyList[Int]) = map(ints)(_ + 1)

  println(add1(Cons(1, Cons(2, Cons(5, Nil)))))

  def map[A,B](l: MyList[A])(f: A => B): MyList[B] = foldRight(l, Nil: MyList[B])((head, acc) =>
    Cons(f(head), acc)
  )

  def filter[A](l: MyList[A])(f: A => Boolean): MyList[A] = l match {
    case Nil => l
    case Cons(head, tail) if f(head) => Cons(head, filter(tail)(f))
    case Cons(_, tail) => filter(tail)(f)
  }

  println(filter(Cons(1, Cons(2, Cons(3, Nil))))(a => a % 2 != 1))
}

