package zeke.runtime;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.nodes.RootNode;

public final class ZkFunction {

    public RootCallTarget callTarget;

    public ZkFunction(RootCallTarget callTarget) {
        this.callTarget = callTarget;
    }

    public static ZkFunction fromRootNode(RootNode node) {
        return new ZkFunction(Truffle.getRuntime().createCallTarget(node));
    }

}
