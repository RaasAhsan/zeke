package zeke;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;
import zeke.nodes.*;

public class Entrypoint {
    public static void main(String[] args) {
        AddNode add = AddNode.of(new StringLiteralNode("hello"), new StringLiteralNode("world"));
        IfNode ifNode = new IfNode(
                EqualsNode.of(new IntLiteralNode(10), AddNode.of(new IntLiteralNode(5), new IntLiteralNode(5))),
                new IntLiteralNode(10),
                add
        );

        ExpressionNode program = new ZkBlockNode(new ExpressionNode[] {
                add,
                ifNode
        });

        ZkRootNode root = new ZkRootNode(program);
        CallTarget target = Truffle.getRuntime().createCallTarget(root);

        System.out.println(target.call());
    }
}
