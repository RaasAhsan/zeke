package zeke.typechecker

import cats.data.NonEmptyList
import munit.FunSuite
import zeke.typechecker.TypeChecker.Typing
import zeke.{Symbol, Syntax, Type}

class TypeCheckerSuite extends FunSuite {

  import Syntax._
  import Type._

  // TODO: pull in munit-scalacheck

  test("integer literals") {
    val result = TypeChecker.typecheckExpression(IntLiteral(10), TypingContext.Empty)
    assertEquals(result, Right(Typing(IntType, TypingContext.Empty)))
  }

  test("block expressions") {
    val expr = Block(NonEmptyList.of(IntLiteral(5), StringLiteral("hello")))
    val result = TypeChecker.typecheckExpression(expr, TypingContext.Empty)
    assertEquals(result, Right(Typing(StringType, TypingContext.Empty)))
  }

  test("block expressions failing") {
    val expr = Block(NonEmptyList.of(Equals(IntLiteral(5), IntLiteral(5)), StringLiteral("hello")))
    val result = TypeChecker.typecheckExpression(expr, TypingContext.Empty)
    assert(result.isLeft)
  }

  test("local variable binding") {
    val result = TypeChecker.typecheckStatement(LetStatement(Symbol("a"), None, IntLiteral(5)), TypingContext.Empty).toOption.get
    assertEquals(result.ty, UnitType)
    assertEquals(result.ctx.getVariableBinding(Symbol("a")), Some(IntType))
  }

//  test("local variable binding in a block") {
//    val expr = Block(NonEmptyList.of(LetStatement(Symbol("a"), None, IntLiteral(5)), GetVariable(Symbol("a"))))
//    val result = TypeChecker.typecheckExpression(expr, TypingContext.Empty).toOption.get
//    assertEquals(result.ty, IntType)
//    assertEquals(result.ctx.getVariableBinding(Symbol("a")), Some(IntType))
//  }
}
