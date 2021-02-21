package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import zeke.runtime.ZkFunction;

public final class InvokeFunctionNode extends ExpressionNode {

    @Child private ExpressionNode functionNode;
    @Children private ExpressionNode[] argumentNodes;
    @Child private IndirectCallNode callNode;

    public InvokeFunctionNode(ExpressionNode functionNode, ExpressionNode[] argumentNodes) {
        this.functionNode = functionNode;
        this.argumentNodes = argumentNodes;
        this.callNode = IndirectCallNode.create();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        ZkFunction function = evaluateFunction(frame);

        Object[] arguments = new Object[argumentNodes.length];
        for (int i = 0; i < argumentNodes.length; i++) {
            arguments[i] = argumentNodes[i].execute(frame);
        }

        return this.callNode.call(function.callTarget, arguments);
    }

    private ZkFunction evaluateFunction(VirtualFrame frame) {
        try {
            return functionNode.executeFunction(frame);
        } catch (UnexpectedResultException e) {
            throw new RuntimeException("expected function");
        }
    }
}
