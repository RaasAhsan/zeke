package zeke.nodes;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

// TODO: specialize this later
public final class GetLocalNode extends ExpressionNode {

    private final FrameSlot slot;

    public GetLocalNode(FrameSlot slot) {
        this.slot = slot;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return frame.getValue(this.slot);
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        try {
            return frame.getBoolean(slot);
        } catch (FrameSlotTypeException e) {
            throw new UnexpectedResultException("local not boolean");
        }
    }
}
