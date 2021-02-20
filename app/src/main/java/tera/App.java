package tera;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.CallTarget;

public class App {
    public static void main(String[] args) {
        AddNode add = AddNode.create(new IntLiteralNode(1), new IntLiteralNode(2));
        TeraRootNode root = new TeraRootNode(add);
        CallTarget target = Truffle.getRuntime().createCallTarget(root);

        System.out.println(target.call());
    }
}
