package zeke

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.FrameDescriptor
import zeke.nodes._
import zeke.parser.ZekeParser
import zeke.runtime.ZkFunction

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
        |""".stripMargin

    println(ZekeParser.parse(input))

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
