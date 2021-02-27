package zeke

import cats.data.NonEmptyList

object Syntax {

  final case class Program(decls: List[TypeDeclaration], statements: List[Statement])

  sealed trait TypeDeclaration

  final case class RecordDeclaration(name: TypeName, projections: List[(Symbol, TypeName)]) extends TypeDeclaration

  sealed trait Statement

  final case class LetStatement(name: Symbol, value: Expression) extends Statement

  final case class ExpressionStatement(expr: Expression) extends Statement

  sealed trait Expression

  final case class Block(exprs: NonEmptyList[Expression]) extends Expression

  final case class If(condition: Expression, consequent: Expression, alternative: Expression) extends Expression

  // Functions

  final case class InvokeFunction(function: Expression, arguments: List[Expression]) extends Expression

  // Variables

  final case class GetVariable(name: Symbol) extends Expression

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

  final case class FunctionLiteral(formalParameters: List[(Symbol, Type)], body: Expression) extends Expression

  // Record operations

  final case class RecordValue(typeName: TypeName, values: List[(Symbol, Expression)]) extends Expression

  final case class RecordProjection(expr: Expression, projection: Symbol) extends Expression

}
