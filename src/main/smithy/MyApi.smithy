$version: "2"

namespace godfinch.industries.attention.spanner

use alloy#simpleRestJson
use alloy#uuidFormat
use smithy4s.meta#refinement

@trait(selector: "string")
structure nonEmptyStringFormat {}

apply godfinch.industries.attention.spanner#nonEmptyStringFormat @refinement(
  targetType: "godfinch.industries.utils.SmithyRefinements.NonEmptyStringFormatR"
)

@uuidFormat
string TodoId

@nonEmptyStringFormat
string TodoName

boolean IsCompleted

@uuidFormat
string TodoListId

@nonEmptyStringFormat
string TodoListName

timestamp ExpiryDate

structure Todo {
  @required
  name: TodoName
  @required
  isCompleted: IsCompleted
}

structure TodoDb {
  @required
  id: TodoId
  @required
  todoListId: TodoListId
  @required
  name: TodoName
  @required
  isCompleted: IsCompleted
}

list Todos {
  member: Todo
}

list AllTodoLists {
  member: TodoListWithId
}

structure TodoList {
  @required
  todoListName: TodoListName
  @required
  expiryDate: ExpiryDate
  @required
  todos: Todos
}

structure TodoListWithId {
  @required
  id: TodoListId
  @required
  todoListName: TodoListName
  @required
  expiryDate: ExpiryDate
}

@simpleRestJson
service TodoListService {
  version: "1.0.0",
  operations: [CreateTodoList, DeleteTodoList, GetTodoList, GetAllTodoLists, UpdateTodoList]
}

@http(method: "POST", uri: "/todos", code: 200)
operation CreateTodoList {
  input: TodoList
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
  todoList: TodoList
}

@http(method: "GET", uri: "/todos/{id}", code: 200)
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

@http(method: "PUT", uri: "/todos/{id}", code: 200)
operation UpdateTodoList {
  input := {
    @required
    @httpLabel
    id: TodoListId
    @required
    todoList: TodoList
  }
}
