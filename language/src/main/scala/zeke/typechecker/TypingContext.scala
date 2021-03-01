package zeke.typechecker

import zeke.{Symbol, Type, TypeName, TypeReference}

final case class TypingContext(variables: Map[Symbol, Type], typeNames: Map[TypeName, Type]) { self =>

  def addVariableBinding(symbol: Symbol, ty: Type): TypingContext =
    copy(variables = variables + (symbol -> ty))

  def addVariableBindings(pairs: List[(Symbol, Type)]): TypingContext =
    pairs.foldLeft(self) { case (ctx, (symbol, ty)) =>
      ctx.addVariableBinding(symbol, ty)
    }

  def getVariableBinding(symbol: Symbol): Option[Type] =
    variables.get(symbol)

  def getTypeByName(name: TypeName): Option[Type] =
    getTypeByReference(TypeReference.Name(name))

  def getTypeByReference(ref: TypeReference): Option[Type] =
    ref match {
      case TypeReference.Name(name) => typeNames.get(name)
      case TypeReference.Function(in, out) =>
        for {
          ity <- getTypeByReference(in)
          oty <- getTypeByReference(out)
        } yield Type.FunctionType(ity, oty)
    }

  // TODO: should this shadow or raise an error? right now we're checking at use-site
  def addTypeDeclaration(typeName: TypeName, ty: Type): TypingContext =
    copy(typeNames = typeNames + (typeName -> ty))
}

object TypingContext {
  val BaseTypeNames = Map(
    TypeName("int") -> Type.IntType,
    TypeName("string") -> Type.StringType,
    TypeName("boolean") -> Type.BooleanType,
    TypeName("unit") -> Type.UnitType
  )

  val Empty = TypingContext(Map(), BaseTypeNames)
}
