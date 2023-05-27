package godfinch.industries

import cats.effect.IO
import godfinch.industries.hello.{HelloWorldService, TodoList, TodoListName}

object HelloWorldImpl extends HelloWorldService[IO] {

  override def createTodos(todoListName: TodoListName, todos: List[Int]): IO[Unit] = ???
  override def getTodos(): IO[TodoList] = ???
}
//  override def getTodos(): IO[Greeting] =
//    Greeting("Fuckayou").pure[IO]