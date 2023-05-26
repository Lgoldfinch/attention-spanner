$version: "2"

namespace com.example.hello

use alloy#simpleRestJson

string Town
string PersonName

@simpleRestJson
service HelloWorldService {
  version: "1.0.0",
  operations: [GetTodos]
}

@http(method: "GET", uri: "/todos", code: 200)
operation GetTodos {
//  input: Person,
  output: Greeting
}

//structure Person {
//  @httpLabel
//  @required
//  name: PersonName,
//

//  @httpQuery("town")
//  town: Town
//}

structure Greeting {
  @required
  message: String
}
