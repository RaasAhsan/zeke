package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;

public final class IfNode extends ExpressionNode {

    @Child private ExpressionNode predicate;
    @Child private ExpressionNode consequent;
    @Child private ExpressionNode alternative;

    private final ConditionProfile profile = ConditionProfile.createBinaryProfile();

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
        if (profile.profile(test)) {
            return consequent.execute(frame);
        } else {
            return alternative.execute(frame);
        }
    }
}
