package zeke.typechecker

import munit.FunSuite
import zeke.{Syntax, Type}

class TypeCheckerSuite extends FunSuite {

  // TODO: pull in scalacheck-effect

  test("integer literals") {
    assertEquals(TypeChecker.typecheckExpression(Syntax.IntLiteral(10), TypingContext.Empty), Right(Type.IntType))
  }

}
