package zeke.typechecker

import zeke.Type

object TypeChecker {

  import zeke.Syntax._

  def typecheckProgram(program: Program): Boolean = {
    ???
  }

  // TODO: we will probably need to return a new typingcontext here
  def typecheckExpression(term: Expression, ctx: TypingContext): Either[String, Type] = {
    term match {
      case IntLiteral(_) => Right(Type.IntType)
      case StringLiteral(_) => Right(Type.StringType)
      case BooleanLiteral(_) => Right(Type.BooleanType)
      case Function(params, body) =>
        val paramTypes = params.map(_._2)
        val newCtx = ctx.addVariableBindings(params)
        typecheckExpression(body, newCtx).map(ty => Type.FunctionType(paramTypes, ty))
      case If(condition, consequent, alternative) => ???
      case _ => Left(s"no typing rules for $term")
    }
  }

}
