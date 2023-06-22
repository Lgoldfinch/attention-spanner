$version: "2"

namespace godfinch.industries.hello

use alloy#simpleRestJson
use alloy#uuidFormat

string TodoListName
string TodoName
@uuidFormat
string TodoListId

list Todos {
  member: TodoName
}

list AllTodoLists {
  member: TodoList
}

@simpleRestJson
service HelloWorldService {
  version: "1.0.0",
  operations: [GetAllTodoLists, CreateTodoList]
}

@http(method: "GET", uri: "/todos", code: 200)
operation GetAllTodoLists {
//  input: Person,
  output: AllTodoListsB
}

//@http(method: "POST", uri: "/todos", code: 200)
//operation DeleteTodos {
//  input: Person,
//}

structure TodoList {
//  @httpLabel
@required
  todoListId: TodoListId
@required
  todoListName: TodoListName
@required
  todos: Todos
}

structure AllTodoListsB {
  @required
  todoLists: AllTodoLists
}

@http(method: "POST", uri: "/todos", code: 200)
operation CreateTodoList {
  input: TodoList
}

//  @httpQuery("town")
//  town: Town
//}

//structure Greeting {
//  @required
//  message: String
//}
