$version: "2"

namespace godfinch.industries.attention.spanner

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

@simpleRestJson
service TodoListService {
  version: "1.0.0",
  operations: [CreateTodoList, DeleteTodoList, GetTodoList, GetAllTodoLists, ]
}

structure CreateTodoListRequest {
  @required
  todoListName: TodoListName
  @required
  todos: Todos
}

@http(method: "POST", uri: "/todos", code: 200)
operation CreateTodoList {
  input: CreateTodoListRequest
}

@http(method: "DELETE", uri: "/todos/{id}", code: 200)
operation DeleteTodoList {
  input := {
    @required
    @httpLabel
    id: TodoListId
  }
}

structure GetTodoListResponse {
  todoList: TodoListDb
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

structure GetAllTodoListsResponse {
  @required
  todoLists: AllTodoLists
}


@http(method: "GET", uri: "/todos", code: 200)
operation GetAllTodoLists {
  output: GetAllTodoListsResponse
}