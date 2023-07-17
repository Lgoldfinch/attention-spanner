$version: "2"

namespace godfinch.industries.hello

use alloy#simpleRestJson
use alloy#uuidFormat

string TodoListName
string TodoName
@uuidFormat
string TodoListId
timestamp TimeCreated

list Todos {
  member: TodoName
}

list AllTodoLists {
  member: CreateTodoListRequest
}

@simpleRestJson
service TodoListService {
  version: "1.0.0",
  operations: [GetTodoList, GetAllTodoLists, CreateTodoList]
}

@http(method: "GET", uri: "/todo/{id}", code: 200)
operation GetTodoList {
  input := {
    @required
    @httpLabel
    id: TodoListId
  }
  output: GetTodoListResponse
}

structure GetTodoListResponse {
//  @required?
   todoList: TodoList
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
  @required
  id: TodoListId
  @required
  todoName: TodoListName
  @required
  createdTimestamp: TimeCreated
  @required
  todos: Todos
}

structure CreateTodoListRequest {
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
  input: CreateTodoListRequest
}

