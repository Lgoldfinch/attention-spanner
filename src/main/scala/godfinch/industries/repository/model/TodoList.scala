package godfinch.industries.repository.model

import godfinch.industries.hello._

case class TodoList(id: TodoListId, todoListName: TodoListName, created: TimeCreated, todos: List[TodoName])
