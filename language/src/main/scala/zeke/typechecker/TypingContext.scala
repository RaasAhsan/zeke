package zeke.typechecker

import zeke.Type

final case class TypingContext(variables: Map[Symbol, Type]) {
  def addVariableBinding(symbol: Symbol, ty: Type): TypingContext =
    copy(variables = variables + (symbol -> ty))
}
