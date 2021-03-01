package zeke

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.FrameDescriptor
import zeke.nodes._
import zeke.parser.ZekeParser
import zeke.runtime.ZkFunction
import zeke.typechecker.TypeChecker

object Launcher {
  def main(args: Array[String]): Unit = {
    val input =
      """
        |
        |record Person {
        |  name: string,
        |  age: int
        |}
        |
        |record Pair {
        |  first: int,
        |  second: int
        |}
        |
        |record PairOfPair {
        |  first: Pair,
        |  second: Pair
        |}
        |
        |record Empty{}
        |
        |let x = true
        |let y = Pair {
        |  first: 34,
        |  second: 54
        |}
        |
        |let z = PairOfPair {
        |  first: y,
        |  second: y
        |}
        |
        |let a = z.first.first
        |let e = Empty {}
        |let p = Person {
        |  name: "Jimmy",
        |  age: 23
        |}
        |
        |let z = (4 + 3) * 5
        |
        |let f = fun(x: int) {
        |  x
        |}
        |
        |let add = fun(x: int) {
        |  fun(y: int) {
        |    x + y
        |  }
        |}
        |
        |let apply = fun(f: int -> int -> int) {
        |  fun(x: int) {
        |    fun(y: int) {
        |      f(x)(y)
        |    }
        |  }
        |}
        |
        |let thunk = fun(x: unit) {
        |  3
        |}
        |
        |add(2)(3)
        |apply(add)(2)(3)
        |thunk(unit)
        |
        |""".stripMargin

    println(input)
    ZekeParser.parse(input) match {
      case Right(program) =>
        println(program)
        println(s"typecheck: ${TypeChecker.typecheckProgram(program)}")
      case Left(err) => println(err)
    }

//    val frameDescriptor = new FrameDescriptor()
//    val program = new ZkBlockNode(Array(
//      new SetLocalNode(frameDescriptor.findOrAddFrameSlot("name"), new IntLiteralNode(2)),
//      new SetLocalNode(frameDescriptor.findOrAddFrameSlot("foo"), new FunctionNode(
//        ZkFunction.fromRootNode(
//          new ZkRootNode(new IntLiteralNode(1337), new FrameDescriptor())
//        )
//      )),
//      new GetLocalNode(frameDescriptor.findFrameSlot("name")),
//      new InvokeFunctionNode(new GetLocalNode(frameDescriptor.findFrameSlot("foo")), Array())
//    ))
//
//    val root = new ZkRootNode(program, frameDescriptor)
//    val target = Truffle.getRuntime().createCallTarget(root)
//
//    println(target.call())
  }
}
