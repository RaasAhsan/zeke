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
    (many(typeDeclaration), many(statement)).mapN((decls, statements) => Program(decls, statements))

  def typeDeclaration: Parser[TypeDeclaration] =
    recordDeclaration

  def recordDeclaration: Parser[RecordDeclaration] = {
    def recordField: Parser[(Symbol, TypeName)] =
      pairBy(symbol, colon, typeName)

    def recordKeyword: Parser[Unit] =
      identifier.filter(_ == "record").void

    recordKeyword ~> (typeName, withBraces(sepBy(recordField, comma))).mapN { (name, fields) =>
      RecordDeclaration(name, fields)
    }
  }

  def statement: Parser[Statement] =
    letStatement | expressionStatement

  def letStatement: Parser[LetStatement] = {
    def letKeyword: Parser[Unit] =
      identifier.filter(_ == "let").void

    (letKeyword ~> symbol, equalsOp ~> expression).mapN { (name, expr) =>
      LetStatement(name, expr)
    }
  }

  def expressionStatement: Parser[ExpressionStatement] =
    expression.map(ExpressionStatement(_))

  def expression: Parser[Expression] =
    booleanLiteral | recordValue | variableExpression

  def variableExpression: Parser[GetVariable] =
    symbol.map(GetVariable(_))

  def booleanLiteral: Parser[BooleanLiteral] = {
    def trueValue: Parser[BooleanLiteral] =
      identifier.filter(_ == "true").as(BooleanLiteral(true))

    def falseValue: Parser[BooleanLiteral] =
      identifier.filter(_ == "false").as(BooleanLiteral(false))

    trueValue | falseValue
  }

  def recordValue: Parser[RecordValue] = {
    def recordFieldAssignment: Parser[(Symbol, Expression)] =
      (symbol <~ colon) ~ expression

    (typeName, withBraces(sepBy(recordFieldAssignment, comma))).mapN(RecordValue(_, _))
  }

  def recordProjection: Parser[RecordProjection] =
    (expression <~ dot, symbol).mapN(RecordProjection(_, _))

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

  def dot: Parser[String] =
    token(string("."))


  def withBraces[A](p: => Parser[A]): Parser[A] =
    bracket(leftBrace, p, rightBrace)

  def leftBrace: Parser[String] =
    token(string("{"))

  def rightBrace: Parser[String] =
    token(string("}"))

}
