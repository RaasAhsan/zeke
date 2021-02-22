package zeke

import cats.data.NonEmptyList

object Syntax {

  final case class Program(exprs: List[Expression])

  sealed trait Expression

  final case class Block(exprs: NonEmptyList[Expression]) extends Expression

  final case class If(condition: Expression, consequent: Expression, alternative: Expression) extends Expression

  // Functions

  final case class Function(formalParameters: List[(Symbol, Type)], body: Expression) extends Expression

  final case class InvokeFunction(function: Expression, arguments: List[Expression]) extends Expression

  // Variables

  // TODO: should there be a distinction between locals and arguments?
  // TODO: change name to a proper Symbol
  final case class BindVariable(name: String, value: Expression) extends Expression

  final case class GetVariable(name: String) extends Expression

  // Arithmetic expressions

  final case class Add(left: Expression, right: Expression) extends Expression

  final case class Subtract(left: Expression, right: Expression) extends Expression

  final case class Multiply(left: Expression, right: Expression) extends Expression

  // Relational operators

  final case class Equals(left: Expression, right: Expression) extends Expression

  final case class NotEquals(left: Expression, right: Expression) extends Expression

  final case class LessThan(left: Expression, right: Expression) extends Expression

  final case class LessThanOrEquals(left: Expression, right: Expression) extends Expression

  final case class GreaterThan(left: Expression, right: Expression) extends Expression

  final case class GreaterThanOrEquals(left: Expression, right: Expression) extends Expression

  // Literals

  final case class IntLiteral(value: Int) extends Expression

  final case class StringLiteral(value: String) extends Expression

  final case class BooleanLiteral(value: Boolean) extends Expression

  final case class UnitLiteral(value: Int) extends Expression

}
