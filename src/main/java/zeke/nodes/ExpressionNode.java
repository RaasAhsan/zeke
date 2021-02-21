package zeke.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import zeke.ZekeTypes;
import zeke.ZekeTypesGen;

@TypeSystemReference(ZekeTypes.class)
public abstract class ExpressionNode extends ZkNode {
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return ZekeTypesGen.expectBoolean(this.execute(frame));
    }
}
