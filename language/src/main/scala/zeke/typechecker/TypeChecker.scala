package zeke.typechecker

import zeke.Type
import zeke.Syntax

object TypeChecker {

  final case class Typing(ty: Type, ctx: TypingContext)

  type TypeCheckResult = Either[String, Typing]

  import Syntax._
  import Type._

  def typecheckProgram(program: Program): Boolean = {
    ???
  }

  // type checking algorithm follows from inversion lemma
  def typecheckExpression(term: Expression, ctx: TypingContext): TypeCheckResult = {
    term match {
      // Literals
      case IntLiteral(_) => Right(Typing(IntType, ctx))
      case StringLiteral(_) => Right(Typing(StringType, ctx))
      case BooleanLiteral(_) => Right(Typing(BooleanType, ctx))
      case FunctionLiteral(params, body) =>
        val paramTys = params.map(_._2)
        val newCtx = ctx.addVariableBindings(params)
        typecheckExpression(body, newCtx).map { ty =>
          Typing(FunctionType(paramTys, ty.ty), ctx) // TODO: in the future when methods exist, add it to a context?
        }

      // Arithmetic expressions
      case Add(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- assertTypesEqual(ty1.ty, IntType)
          _ <- assertTypesEqual(ty1.ty, ty2.ty)
        } yield Typing(IntType, ctx)
      case Subtract(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- assertTypesEqual(ty1.ty, IntType)
          _ <- assertTypesEqual(ty1.ty, ty2.ty)
        } yield Typing(IntType, ctx)
      case Multiply(left, right) =>
        for {
          ty1 <- typecheckExpression(left, ctx)
          ty2 <- typecheckExpression(right, ctx)
          _ <- assertTypesEqual(ty1.ty, IntType)
          _ <- assertTypesEqual(ty1.ty, ty2.ty)
        } yield Typing(IntType, ctx)

      case If(condition, consequent, alternative) =>
        for {
          ty1 <- typecheckExpression(condition, ctx)
          ty2 <- typecheckExpression(consequent, ctx)
          ty3 <- typecheckExpression(alternative, ctx)
          _ <- assertTypesEqual(ty1.ty, BooleanType)
          _ <- assertTypesEqual(ty2.ty, ty3.ty)
        } yield Typing(ty2.ty, ctx)

      case Block(exprs) =>
        exprs.foldLeft[TypeCheckResult](Right(Typing(UnitType, ctx))) {
          case (Right(typing), expr) => typecheckExpression(expr, typing.ctx)
          case (l @ Left(_), _) => l
        }

      // Variables
      case GetVariable(name) =>
        ctx.getVariable(name).fold[TypeCheckResult](Left(s"variable $name not found"))(ty => Right(Typing(ty, ctx)))

      case BindVariable(name, value) =>
        typecheckExpression(value, ctx).map(ty => Typing(UnitType, ctx.addVariableBinding(name, ty.ty)))

      case _ => Left(s"no typing rules for $term")
    }
  }

  private def assertTypesEqual(ty1: Type, ty2: Type): Either[String, Unit] =
    if (ty1 == ty2) Right(()) else Left(s"$ty1 does not equal $ty2")

}
