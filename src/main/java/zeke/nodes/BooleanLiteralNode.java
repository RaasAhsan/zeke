package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class BooleanLiteralNode extends ExpressionNode {
    public final boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.value;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) {
        return this.value;
    }
}
