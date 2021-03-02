package zeke.typechecker

import cats.syntax.all._
import zeke.Type
import zeke.Syntax
import zeke.Symbol

object TypeChecker {

  final case class Typing(ty: Type, ctx: TypingContext)

  type TypeCheckResult = Either[String, Typing]

  import Syntax._
  import Type._

  def typecheckProgram(program: Program): Either[String, Unit] = {
    val initCtx = TypingContext.Empty
    val result = for {
      r1 <- program.decls.foldLeft[TypeCheckResult](Right(Typing(UnitType, initCtx))) {
        case (Right(typing), expr) => typecheckTypeDeclaration(expr, typing.ctx)
        case (l @ Left(_), _) => l
      }
      _ <- program.statements.foldLeft[TypeCheckResult](Right(Typing(UnitType, r1.ctx))) {
        case (Right(typing), expr) => typecheckStatement(expr, typing.ctx)
        case (l @ Left(_), _) => l
      }
    } yield ()
    result
  }

  def typecheckTypeDeclaration(term: TypeDeclaration, ctx: TypingContext): TypeCheckResult = {
    term match {
      case RecordDeclaration(name, projections) =>
        val projDistinct = projections.distinctBy(_._1)
        if (projDistinct.size == projections.distinct.length) {
          val projTypes = projDistinct.foldLeft[Either[String, Map[Symbol, Type]]](Right(Map())) { case (acc, (symbol, typeName)) =>
            for {
              map <- acc
              ty <- ctx.getTypeByReference(typeName) match {
                case Some(ty) => Right(ty)
                case None => Left("type not found")
              }
            } yield map + (symbol -> ty)
          }
          projTypes.map { types =>
            val ty = RecordType(name, types)
            Typing(ty, ctx.addTypeDeclaration(name, ty))
          }
        } else {
          Left(s"same field name was declared in record $name")
        }
    }
  }

  def typecheckStatement(term: Statement, ctx: TypingContext): TypeCheckResult =
    term match {
      case ExpressionStatement(expr) => typecheckExpression(expr, ctx)
      case LetStatement(name, letType, value) =>
        for {
          ty <- typecheckExpression(value, ctx).map(ty => Typing(UnitType, ctx.addVariableBinding(name, ty.ty)))
          _ <- letType match {
            case Some(typeName) =>
              ctx.getTypeByName(typeName).fold[Either[String, Unit]](Left("type not found")) { lty =>
                if (ty.ty == lty) Right(()) else Left(s"$lty does not match expected type $ty")
              }
            case None => Right(())
          }
        } yield ty
    }

  // type checking algorithm follows from inversion lemma
  def typecheckExpression(term: Expression, ctx: TypingContext): TypeCheckResult = {
    term match {
      // Literals
      case IntLiteral(_) => Right(Typing(IntType, ctx))
      case StringLiteral(_) => Right(Typing(StringType, ctx))
      case BooleanLiteral(_) => Right(Typing(BooleanType, ctx))
      case FunctionLiteral(sym, tr, returnType, body) =>
        for {
          ty <- ctx.getTypeByReference(tr).fold[Either[String, Type]](Left("type not found"))(Right(_))
          rty <- typecheckExpression(body, ctx.addVariableBinding(sym, ty))
          _ <- returnType match {
            case Some(typeName) =>
              ctx.getTypeByName(typeName).fold[Either[String, Unit]](Left("type not found")) { lty =>
                if (rty.ty == lty) Right(()) else Left(s"$lty does not match expected type $ty")
              }
            case None => Right(())
          }
        } yield Typing(FunctionType(ty, rty.ty), ctx) // TODO: in the future when methods exist, add it to a context?
      case UnitLiteral() => Right(Typing(UnitType, ctx))

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
        ctx.getVariableBinding(name).fold[TypeCheckResult](Left(s"variable $name not found"))(ty => Right(Typing(ty, ctx)))

      // Records
      case RecordLiteral(name, values) =>
        for {
          ty <- ctx.getTypeByName(name) match {
            case Some(ty) => Right(ty)
            case None => Left (s"unknown type $name")
          }
          rty <- assertRecordType(ty)
          // TODO: ordering of fields is insignificant for now, probably not a huge deal
          ftys <- values.map { case (sym, expr) =>
            typecheckExpression(expr, ctx).map(typ => sym -> typ.ty)
          }.sequence
          _ <- if (ftys.toMap == rty.projections) Right(()) else Left("record projections don't match")
        } yield Typing(rty, ctx)

      case RecordProjection(expr, proj) =>
        for {
          typ <- typecheckExpression(expr, ctx)
          rty <- assertRecordType(typ.ty)
          pty <- rty.projections.get(proj) match {
            case Some(ty) => Right(ty)
            case None => Left("field not part of record")
          }
        } yield Typing(pty, ctx)

      case InvokeFunction(function, param) =>
        for {
          typ <- typecheckExpression(function, ctx)
          fty <- typ.ty match {
            case f: FunctionType => Right(f)
            case _ => Left("function type not found")
          }
          pty <- typecheckExpression(param, ctx)
          _ <- if (fty.in == pty.ty) Right(()) else Left(s"expected: ${fty.in}, actual: ${pty.ty}")
        } yield Typing(fty.out, ctx)

      case _ => Left(s"no typing rules for $term")
    }
  }

  private def assertTypesEqual(ty1: Type, ty2: Type): Either[String, Unit] =
    if (ty1 == ty2) Right(()) else Left(s"$ty1 does not equal $ty2")

  private def assertRecordType(ty: Type): Either[String, RecordType] =
    ty match {
      case r @ RecordType(_, _) => Right(r)
      case _ => Left("not a record")
    }

}
