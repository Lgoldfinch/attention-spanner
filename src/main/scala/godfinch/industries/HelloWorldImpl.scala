package godfinch.industries

import godfinch.industries.hello._

final class HelloWorldImpl[F[_]] extends HelloWorldService[F] {

  override def createTodos(todoListName: TodoListName, todos: List[TodoName]): F[Unit] = ???
    
  override def getTodos(): F[TodoList] = ???
}