package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public abstract class ExpressionNode extends ZekeNode {
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        Object result = execute(frame);
        if (result instanceof Boolean) {
            return (boolean) result;
        } else {
            throw new UnexpectedResultException(result);
        }
    }
}
