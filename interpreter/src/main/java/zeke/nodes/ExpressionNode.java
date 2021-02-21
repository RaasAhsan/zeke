package zeke.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import zeke.ZkTypes;
import zeke.ZkTypesGen;
import zeke.runtime.ZkFunction;

@TypeSystemReference(ZkTypes.class)
public abstract class ExpressionNode extends ZkNode {
    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        return ZkTypesGen.expectBoolean(this.execute(frame));
    }

    public ZkFunction executeFunction(VirtualFrame frame) throws UnexpectedResultException {
        return ZkTypesGen.expectZkFunction(this.execute(frame));
    }
}
