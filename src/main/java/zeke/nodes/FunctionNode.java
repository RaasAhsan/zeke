package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import zeke.runtime.ZkFunction;

public final class FunctionNode extends ExpressionNode {

    private ZkFunction function;

    public FunctionNode(ZkFunction function) {
        this.function = function;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.function;
    }

    @Override
    public ZkFunction executeFunction(VirtualFrame frame) {
        return this.function;
    }

}
