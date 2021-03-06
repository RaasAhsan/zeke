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
        |variant OptionInt {
        |  Some(int),
        |  None(unit)
        |}
        |
        |let x: OptionInt = OptionInt::Some(3)
        |let y = OptionInt::None(unit)
        |
        |let z = match x {
        |  case Some(a) => a,
        |  case None(x) => 0
        |}
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
