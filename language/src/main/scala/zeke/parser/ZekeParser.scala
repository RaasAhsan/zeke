package zeke.parser

import atto._
import Atto._
import cats.syntax.all._
import zeke._

object ZekeParser {

  import zeke.Syntax._

  def parse(input: String): Either[String, Program] =
    entrypoint.parseOnly(input).either

  def entrypoint: Parser[Program] =
    skipWhitespace ~> program <~ endOfInput

  def program: Parser[Program] =
    (many(typeDeclaration), many(expression)).mapN((decls, exprs) => Program(decls, exprs))

  def typeDeclaration: Parser[TypeDeclaration] =
    recordDeclaration

  def recordDeclaration: Parser[RecordDeclaration] =
    recordKeyword ~> (typeName, withBraces(sepBy(recordField, comma))).mapN { (name, fields) =>
      RecordDeclaration(name, fields)
    }

  def recordField: Parser[(Symbol, TypeName)] =
    pairBy(symbol, colon, typeName)

  def recordKeyword: Parser[Unit] =
    identifier.filter(_ == "record").void

  def withBraces[A](p: => Parser[A]): Parser[A] =
    bracket(leftBrace, p, rightBrace)

  def leftBrace: Parser[String] =
    token(string("{"))

  def rightBrace: Parser[String] =
    token(string("}"))

  def expression: Parser[Expression] =
    letExpression | booleanLiteral | variableExpression

  def letExpression: Parser[BindVariable] =
    (letKeyword ~> symbol, equalsOp ~> expression).mapN { (name, expr) =>
      BindVariable(name, expr)
    }

  def letKeyword: Parser[Unit] =
    identifier.filter(_ == "let").void

  def variableExpression: Parser[GetVariable] =
    symbol.map(GetVariable(_))

  def booleanLiteral: Parser[BooleanLiteral] =
    trueValue | falseValue

  def trueValue: Parser[BooleanLiteral] =
    identifier.filter(_ == "true").as(BooleanLiteral(true))

  def falseValue: Parser[BooleanLiteral] =
    identifier.filter(_ == "false").as(BooleanLiteral(false))

  // TODO: need to generalize this
//  def numericExpression: Parser[Expression] =
//    addExpression
//
//  def addExpression: Parser[Add] =


  // Identifiers

  def typeName: Parser[TypeName] =
    identifier.map(TypeName(_))

  def symbol: Parser[Symbol] =
    identifier.map(Symbol(_))

  def identifier: Parser[String] =
    token(stringOf1(letter))

  def colon: Parser[String] =
    token(string(":"))

  def comma: Parser[String] =
    token(string(","))

  def equalsOp: Parser[Unit] =
    op.filter(_ == "=").void

  def op: Parser[String] =
    token(stringOf1(oneOf("=+<>*-/")))

}
