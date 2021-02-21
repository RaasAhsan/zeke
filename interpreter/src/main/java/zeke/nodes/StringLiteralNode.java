package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class StringLiteralNode extends ExpressionNode {
    public final String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return value;
    }
}
