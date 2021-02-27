package zeke.parser

import atto._
import Atto._
import cats.syntax.all._
import zeke._

object ZekeParser {

  import zeke.Syntax._

  def parseProgram(input: String): Either[String, Program] =
    program.parseOnly(input).either

  def program: Parser[Program] =
    skipWhitespace ~> (many(typeDeclaration)).map((decls) => Program(decls, List()))

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

  def withBraces[A](p: => Parser[A]): Parser[A] =
    bracket(leftBrace, p, rightBrace)

  def leftBrace: Parser[String] =
    token(string("{"))

  def rightBrace: Parser[String] =
    token(string("}"))

  def expression: Parser[Expression] = ???

}
