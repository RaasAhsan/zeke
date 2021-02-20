package zeke;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;
import zeke.nodes.AddNode;
import zeke.nodes.IntLiteralNode;
import zeke.nodes.StringLiteralNode;
import zeke.nodes.ZekeRootNode;

public class Entrypoint {
    public static void main(String[] args) {
        AddNode add = AddNode.create(new StringLiteralNode("hello"), new StringLiteralNode("world"));

        ZekeRootNode root = new ZekeRootNode(add);
        CallTarget target = Truffle.getRuntime().createCallTarget(root);

        System.out.println(target.call());
    }
}
