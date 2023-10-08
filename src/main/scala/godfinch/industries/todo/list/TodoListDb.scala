package godfinch.industries.todo.list

import cats.Show
import derevo.Derivation
import godfinch.industries.attention.spanner.{ExpiryDate, TodoListId, TodoListName, TodoListWithId}
import godfinch.industries.utils.uuid.Smithy4sNewTypeDerivation

//@derive(uuid, show)
final case class TodoListDb(
                     id: TodoListId,
                     name: TodoListName,
                     expiryDate: ExpiryDate
                 ) {
  def toTodoListWithId: TodoListWithId = TodoListWithId(id, name, expiryDate)
}

object TodoListDb {
  implicit val showTodoDb: Show[TodoListDb] = new Show[TodoListDb] {
    override def show(t: TodoListDb): String = t.toString
  }
}

  trait Derive[F[_]]
    extends Derivation[F]
      with Smithy4sNewTypeDerivation[F] {
    def instance(implicit ev: OnlyNewtypes): Nothing = ev.absurd

//    @implicitNotFound("Only newtypes instances can be derived")
    abstract final class OnlyNewtypes {
      def absurd: Nothing = ???
    }
}
