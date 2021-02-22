package zeke.typechecker

import zeke.{Symbol, Type}

final case class TypingContext(variables: Map[Symbol, Type]) { self =>

  def addVariableBinding(symbol: Symbol, ty: Type): TypingContext =
    copy(variables = variables + (symbol -> ty))

  def addVariableBindings(pairs: List[(Symbol, Type)]): TypingContext =
    pairs.foldLeft(self) { case (ctx, (symbol, ty)) =>
      ctx.addVariableBinding(symbol, ty)
    }

  def getVariable(symbol: Symbol): Option[Type] =
    variables.get(symbol)
}

object TypingContext {
  val Empty = TypingContext(Map())
}
