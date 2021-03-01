package zeke

sealed trait TypeReference

object TypeReference {
  final case class Name(name: TypeName) extends TypeReference

  final case class Function(in: TypeReference, out: TypeReference) extends TypeReference
}
