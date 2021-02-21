package zeke;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import zeke.nodes.*;
import zeke.runtime.ZkFunction;

public class Entrypoint {
    public static void main(String[] args) {
        FrameDescriptor frameDescriptor = new FrameDescriptor();

        AddNode add = AddNode.of(new StringLiteralNode("hello"), new StringLiteralNode("world"));
        IfNode ifNode = new IfNode(
                EqualsNode.of(new IntLiteralNode(10), AddNode.of(new IntLiteralNode(5), new IntLiteralNode(5))),
                new IntLiteralNode(10),
                add
        );

        ExpressionNode program = new ZkBlockNode(new ExpressionNode[] {
                new SetLocalNode(frameDescriptor.findOrAddFrameSlot("name"), new IntLiteralNode(2)),
                new SetLocalNode(frameDescriptor.findOrAddFrameSlot("foo"), new FunctionNode(
                        ZkFunction.fromRootNode(
                                new ZkRootNode(new IntLiteralNode(1337), new FrameDescriptor())
                        )
                )),
                add,
                new GetLocalNode(frameDescriptor.findFrameSlot("name")),
                new InvokeFunctionNode(new GetLocalNode(frameDescriptor.findFrameSlot("foo")), new ExpressionNode[]{})
        });

        ZkRootNode root = new ZkRootNode(program, frameDescriptor);
        CallTarget target = Truffle.getRuntime().createCallTarget(root);

        System.out.println(target.call());
    }
}
