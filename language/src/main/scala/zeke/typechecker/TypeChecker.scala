package zeke.typechecker

import zeke.Type

object TypeChecker {

  import zeke.Syntax._

  def typecheckProgram(program: Program): Boolean = {
    ???
  }

  // type checking algorithm follows from inversion lemma
  def typecheckExpression(term: Expression, ctx: TypingContext): Either[String, Type] = {
    term match {
      // Literals
      case IntLiteral(_) => Right(Type.IntType)
      case StringLiteral(_) => Right(Type.StringType)
      case BooleanLiteral(_) => Right(Type.BooleanType)
      case FunctionLiteral(params, body) =>
        val paramTys = params.map(_._2)
        val newCtx = ctx.addVariableBindings(params)
        typecheckExpression(body, newCtx).map(ty => Type.FunctionType(paramTys, ty))

      // Arithmetic expressions
      case Add(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- if (ty1 == Type.IntType && ty2 == Type.IntType) Right(()) else Left("lhs and rhs aren't both integers")
        } yield Type.IntType
      case Subtract(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- if (ty1 == Type.IntType && ty2 == Type.IntType) Right(()) else Left("lhs and rhs aren't both integers")
        } yield Type.IntType
      case Multiply(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- if (ty1 == Type.IntType && ty2 == Type.IntType) Right(()) else Left("lhs and rhs aren't both integers")
        } yield Type.IntType

      case If(condition, consequent, alternative) =>
        for {
          ty1 <- typecheckExpression(condition, ctx)
          ty2 <- typecheckExpression(consequent, ctx)
          ty3 <- typecheckExpression(alternative, ctx)
          _ <- if (ty1 == Type.BooleanType && ty2 == ty3) Right(()) else Left("if types don't check")
        } yield ty2

      // Variables
      case GetVariable(name) =>
        ctx.getVariable(name).fold[Either[String, Type]](Left(s"variable $name not found"))(Right(_))

      case BindVariable(name, value) =>
        // TODO: we will probably need to return a new typingcontext here
        ???

      case _ => Left(s"no typing rules for $term")
    }
  }

}
