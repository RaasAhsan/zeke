package zeke.typechecker

import munit.FunSuite
import zeke.{Syntax, Type}

class TypeCheckerSuite extends FunSuite {

  import Syntax._
  import Type._

  // TODO: pull in munit-scalacheck

  test("integer literals") {
    assertEquals(TypeChecker.typecheckExpression(IntLiteral(10), TypingContext.Empty), Right(IntType))
  }

}
