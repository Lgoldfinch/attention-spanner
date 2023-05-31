$version: "2"

namespace godfinch.industries.hello

use alloy#simpleRestJson

string TodoListName
string TodoName

list IntList {
  member: TodoName
}

@simpleRestJson
service HelloWorldService {
  version: "1.0.0",
  operations: [GetTodos, CreateTodos]
}

@http(method: "GET", uri: "/todos", code: 200)
operation GetTodos {
//  input: Person,
  output: TodoList
}

//@http(method: "POST", uri: "/todos", code: 200)
//operation DeleteTodos {
//  input: Person,
//}

structure TodoList {
//  @httpLabel
  @required
  todoListName: TodoListName
@required
  todos: IntList
}

@http(method: "POST", uri: "/todos", code: 200)
operation CreateTodos {
  input: TodoList
}

//  @httpQuery("town")
//  town: Town
//}

//structure Greeting {
//  @required
//  message: String
//}
