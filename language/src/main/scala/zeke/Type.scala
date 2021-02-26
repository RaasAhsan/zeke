package zeke

sealed trait Type

object Type {

  case object IntType extends Type

  case object BooleanType extends Type

  case object StringType extends Type

  case object UnitType extends Type

  final case class FunctionType(argumentTypes: List[Type], returnType: Type) extends Type

  final case class RecordType(name: TypeName, projections: Map[Symbol, Type]) extends Type

}
