package zeke.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import zeke.runtime.Unit;

// TODO: specialize this later
public final class SetLocalNode extends ExpressionNode {

    private final FrameSlot slot;
    @Child private ExpressionNode expr;

    public SetLocalNode(FrameSlot slot, ExpressionNode expr) {
        this.slot = slot;
        this.expr = expr;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        frame.getFrameDescriptor().setFrameSlotKind(this.slot, FrameSlotKind.Object);
        frame.setObject(this.slot, this.expr.execute(frame));
        return Unit.SINGLETON;
    }
}
