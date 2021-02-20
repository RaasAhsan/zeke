package tera;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class IntLiteralNode extends TeraNode {
    public final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return value;
    }
}
