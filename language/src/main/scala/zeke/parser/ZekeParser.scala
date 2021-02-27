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
    (many(typeDeclaration), many((endOfInput ~> err[Statement]("end")) | statement)).mapN((decls, statements) => Program(decls, statements))

  def typeDeclaration: Parser[TypeDeclaration] =
    recordDeclaration

  def recordDeclaration: Parser[RecordDeclaration] = {
    def field: Parser[(Symbol, TypeName)] =
      pairBy(symbol, colon, typeName)

    def keyword: Parser[Unit] =
      identifier.filter(_ == "record").void

    keyword ~> (typeName, withBraces(sepBy(field, comma))).mapN { (name, fields) =>
      RecordDeclaration(name, fields)
    }
  }

  // Statements

  def statement: Parser[Statement] =
    (letStatement | expressionStatement)

  def letStatement: Parser[LetStatement] = {
    def letKeyword: Parser[Unit] =
      identifier.filter(_ == "let").void

    (letKeyword ~> symbol, equalsOp ~> expression).mapN { (name, expr) =>
      LetStatement(name, expr)
    }
  }

  def expressionStatement: Parser[ExpressionStatement] =
    expression.map(ExpressionStatement(_))

  // Expressions

  def expression: Parser[Expression] =
    additiveExpression

  def additiveExpression: Parser[Expression] =
    (multiplicativeExpression, ((plus.as(true) | minus.as(false)) ~ multiplicativeExpression).many).mapN { (k, ks) =>
      ks.foldLeft(k) { case (acc, (b, expr)) =>
        if (b) Add(acc, expr) else Subtract(acc, expr)
      }
    }

  def multiplicativeExpression: Parser[Expression] =
    (dotExpression, (star ~> dotExpression).many).mapN { (k, ks) =>
      ks.foldLeft(k) { case (acc, expr) =>
        Multiply(acc, expr)
      }
    }

  def dotExpression: Parser[Expression] =
    (primary, (dot ~> symbol).many).mapN { (k, ks) =>
      ks.foldLeft(k) { case (acc, sym) =>
        RecordProjection(acc, sym)
      }
    }

  def primary: Parser[Expression] =
    booleanLiteral | integerLiteral | stringLiteralP | recordLiteral | variableExpression | withParens(expression)

  def integerLiteral: Parser[IntLiteral] =
    token(int.map(IntLiteral(_)))

  def stringLiteralP: Parser[StringLiteral] =
    token(stringLiteral.map(StringLiteral(_)))

  def variableExpression: Parser[GetVariable] =
    token(symbol.map(GetVariable(_)))

  def booleanLiteral: Parser[BooleanLiteral] = {
    val a1 = identifier.filter(_ == "true").as(BooleanLiteral(true))
    val a2 = identifier.filter(_ == "false").as(BooleanLiteral(false))
    a1 | a2
  }

  def recordLiteral: Parser[RecordLiteral] = {
    def recordFieldAssignment: Parser[(Symbol, Expression)] =
      (symbol <~ colon) ~ expression

    (typeName, withBraces(sepBy(recordFieldAssignment, comma))).mapN(RecordLiteral(_, _))
  }

  def recordProjection: Parser[RecordProjection] =
    (expression <~ dot, symbol).mapN(RecordProjection(_, _))

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

  def plus: Parser[Unit] =
    op.filter(_ == "+").void

  def minus: Parser[Unit] =
    op.filter(_ == "-").void

  def star: Parser[Unit] =
    op.filter(_ == "*").void

  def op: Parser[String] =
    token(stringOf1(oneOf("=+<>*-/")))

  def dot: Parser[String] =
    token(string("."))


  def withBraces[A](p: => Parser[A]): Parser[A] =
    bracket(token(string("{")), p, token(string("}")))

  def withParens[A](p: => Parser[A]): Parser[A] =
    bracket(token(string("(")), p, token(string(")")))

  def debug = get.map(x => {
    println(x)
    println(x.length)
    ()
  })

}
