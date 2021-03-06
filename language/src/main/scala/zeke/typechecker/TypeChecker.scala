package zeke.typechecker

import cats.syntax.all._
import zeke.{Symbol, Syntax, Type, TypeName}

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
        if (projections.distinctBy(_._1).size == projections.length) {
          val projTypes = projections.map { case (symbol, typeRef) =>
            ctx.getTypeByReference(typeRef).fold[Either[String, (Symbol, Type)]](Left("type not found"))(ty => Right(symbol -> ty))
          }.sequence
          projTypes.map { types =>
            val ty = RecordType(name, types.toMap)
            Typing(ty, ctx.addTypeDeclaration(name, ty))
          }
        } else {
          Left(s"same field name was declared in record $name")
        }
      case VariantDeclaration(name, members) =>
        if (members.distinctBy(_._1).size == members.length) {
          val memberTypes = members.map { case (typeName, typeRef) =>
            ctx.getTypeByReference(typeRef).fold[Either[String, (TypeName, Type)]](Left("type not found"))(ty => Right(typeName -> ty))
          }.sequence
          memberTypes.map { types =>
            val ty = VariantType(name, types.toMap)
            Typing(ty, ctx.addTypeDeclaration(name, ty))
          }
        } else {
          Left(s"same member name was declared more than once in record $name")
        }
    }
  }

  def typecheckStatement(term: Statement, ctx: TypingContext): TypeCheckResult =
    term match {
      case ExpressionStatement(expr) => typecheckExpression(expr, ctx)
      case LetStatement(name, letType, value) =>
        for {
          ty <- typecheckExpression(value, ctx)
          _ <- letType match {
            case Some(typeName) =>
              ctx.getTypeByName(typeName).fold[Either[String, Unit]](Left(s"type ${typeName} not found")) { lty =>
                assertTypesEqual(ty.ty, lty)
              }
            case None => Right(())
          }
        } yield Typing(UnitType, ctx.addVariableBinding(name, ty.ty))
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
                assertTypesEqual(rty.ty, lty)
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

      // Variants

      case VariantLiteral(typeName, memberName, value) =>
        for {
          ty <- ctx.getTypeByName(typeName) match {
            case Some(ty) => Right(ty)
            case None => Left (s"unknown type $typeName")
          }
          vty <- assertVariantType(ty)
          ety <- vty.members.get(memberName).fold[Either[String, Type]](Left(s"invalid variant $memberName"))(Right(_))
          bty <- typecheckExpression(value, ctx)
          _ <- assertTypesEqual(bty.ty, ety)
        } yield Typing(vty, ctx)

      case Match(expr, clauses) =>
        for {
          ety <- typecheckExpression(expr, ctx)
          vty <- assertVariantType(ety.ty)
          _ <- if (clauses.length == 0) Left("need at least one clause") else Right(())
          _ <- {
            val matchedMembers = clauses.collect {
              case (MemberPattern(typeName, _), _) => typeName
            }
            if (matchedMembers.distinct.length == matchedMembers.length) {
              val s1 = matchedMembers.toSet
              val s2 = vty.members.keySet
              if (s1 == s2) {
                clauses.find(_._1 == WildcardPattern).fold[Either[String, Unit]](Right(()))(_ => Left("unneeded wildcard pattern"))
              } else {
                if (s1.subsetOf(s2)) {
                  clauses.find(_._1 == WildcardPattern).fold[Either[String, Unit]](Left("need a wildcard case"))(_ => Right(()))
                } else {
                  Left("unknown variant case found")
                }
              }
            } else {
              Left("repeated match clause")
            }
          }
          ctys <- clauses.map { case (pattern, expr) =>
            val nextCtx = pattern match {
              case MemberPattern(typeName, symbol) =>
                ctx.addVariableBinding(symbol, vty.members.get(typeName).get) // TODO: fix this get
              case WildcardPattern => ctx
            }
            typecheckExpression(expr, nextCtx)
          }.sequence
          _ <- if (ctys.map(_.ty).toSet.size == 1) Right(()) else Left("Branch types must match")
        } yield Typing(ctys.head.ty, ctx)

      case FunctionApply(function, param) =>
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

  private def assertVariantType(ty: Type): Either[String, VariantType] =
    ty match {
      case v @ VariantType(_, _) => Right(v)
      case _ => Left("not a variant")
    }

}
