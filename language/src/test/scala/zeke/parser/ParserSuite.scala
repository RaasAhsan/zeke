package zeke.parser

import munit.FunSuite
import atto._
import Atto._
import zeke.{Symbol, Syntax, Type, TypeName}

class ParserSuite extends FunSuite {

  // TODO: property based tests

  import ZekeParser._
  import zeke.Syntax._

  test("parse let statement") {
    val input = "let x = 3"
    val expected = LetStatement(Symbol("x"), None, IntLiteral(3))
    assertEquals(letStatement.parseOnly(input).either, Right(expected))
  }

  test("parse let statement with explicit type") {
    val input = "let x: int = 3"
    val expected = LetStatement(Symbol("x"), Some(TypeName("int")), IntLiteral(3))
    assertEquals(letStatement.parseOnly(input).either, Right(expected))
  }

}
