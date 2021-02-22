package zeke.typechecker

import zeke.Type

object TypeChecker {

  import zeke.Syntax._

  def typecheckProgram(program: Program): Boolean = {
    ???
  }

  def typecheckExpression(term: Expression, ctx: TypingContext): Either[String, Type] = {
    term match {
      case IntLiteral(_) => Right(Type.IntType)
      case StringLiteral(_) => Right(Type.StringType)
      case BooleanLiteral(_) => Right(Type.BooleanType)
      case Function(parameters, body) =>
        val parameterTypes = parameters.map(_._2)
        typecheckExpression(body, ctx).map(ty => Type.FunctionType(parameterTypes, ty))
      case If(condition, consequent, alternative) => ???
      case _ => Left(s"no typing rules for $term")
    }
  }

}
