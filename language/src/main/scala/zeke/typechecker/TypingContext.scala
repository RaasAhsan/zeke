package zeke.typechecker

import zeke.{Symbol, Type, TypeName}

final case class TypingContext(variables: Map[Symbol, Type], typeDeclarations: Map[TypeName, Type]) { self =>

  def addVariableBinding(symbol: Symbol, ty: Type): TypingContext =
    copy(variables = variables + (symbol -> ty))

  def addVariableBindings(pairs: List[(Symbol, Type)]): TypingContext =
    pairs.foldLeft(self) { case (ctx, (symbol, ty)) =>
      ctx.addVariableBinding(symbol, ty)
    }

  def getVariableBinding(symbol: Symbol): Option[Type] =
    variables.get(symbol)

  def getTypeForName(name: TypeName): Option[Type] =
    typeDeclarations.get(name)

  // TODO: should this shadow or raise an error? right now we're checking at use-site
  def addTypeDeclaration(typeName: TypeName, ty: Type): TypingContext =
    copy(typeDeclarations = typeDeclarations + (typeName -> ty))
}

object TypingContext {
  val DefaultTypeDeclarations = Map(
    TypeName("int") -> Type.IntType,
    TypeName("string") -> Type.StringType,
    TypeName("boolean") -> Type.BooleanType,
    TypeName("unit") -> Type.UnitType
  )

  val Empty = TypingContext(Map(), DefaultTypeDeclarations)
}
