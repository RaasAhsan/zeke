package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;

public final class ZkBlockNode extends ExpressionNode implements BlockNode.ElementExecutor<ExpressionNode> {

    @Child private BlockNode<ExpressionNode> block;

    // Precondition: exprs is nonempty
    public ZkBlockNode(ExpressionNode[] exprs) {
        this.block = BlockNode.create(exprs, this);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return this.block.executeGeneric(frame, BlockNode.NO_ARGUMENT);
    }

    @Override
    public void executeVoid(VirtualFrame frame, ExpressionNode node, int index, int argument) {
        node.execute(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame, ExpressionNode node, int index, int argument) {
        return node.execute(frame);
    }
}
