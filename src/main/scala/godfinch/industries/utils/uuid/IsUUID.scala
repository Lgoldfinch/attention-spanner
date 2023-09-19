package godfinch.industries.utils.uuid

import godfinch.industries.attention.spanner.{TodoId, TodoListId}
import monocle.Iso

import java.util.UUID

trait IsUUID[A] {
  def _UUID: Iso[UUID, A]
}

// Packaging type class instances within IsUUID means you don't have to import instances into scope when using ID.make
object IsUUID {
  def apply[A: IsUUID]: IsUUID[A] = implicitly

  implicit val identityUUID: IsUUID[UUID] = new IsUUID[UUID] {
    val _UUID = Iso[UUID, UUID](identity)(identity)
  }

  implicit val todoId: IsUUID[TodoId] = new IsUUID[TodoId] {
    override def _UUID: Iso[UUID, TodoId] = Iso[UUID, TodoId](TodoId.apply)(_.value)
  }

  implicit val todoListId: IsUUID[TodoListId] = new IsUUID[TodoListId] {
    override def _UUID: Iso[UUID, TodoListId] = Iso[UUID, TodoListId](TodoListId.apply)(_.value)
  }
}