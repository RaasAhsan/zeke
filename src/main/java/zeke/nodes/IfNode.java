package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public final class IfNode extends ExpressionNode {

    @Child
    private ExpressionNode predicate;
    @Child
    private ExpressionNode consequent;
    @Child
    private ExpressionNode alternative;

    public IfNode(ExpressionNode predicate, ExpressionNode consequent, ExpressionNode alternative) {
        this.predicate = predicate;
        this.consequent = consequent;
        this.alternative = alternative;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        boolean test;
        try {
            test = predicate.executeBoolean(frame);
        } catch (UnexpectedResultException e) {
            throw new RuntimeException("Expected boolean");
        }

        // A type checker must assert that the types of both subexpressions match
        if (test) {
            return consequent.execute(frame);
        } else {
            return alternative.execute(frame);
        }
    }
}
