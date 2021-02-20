package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class EqualsNode extends BinaryNode {
    public static EqualsNode of(ExpressionNode left, ExpressionNode right) {
        return EqualsNodeGen.create(left, right);
    }

    @Specialization
    protected boolean doInt(int left, int right) {
        return left == right;
    }

    @Specialization
    protected boolean doString(String left, String right) {
        return left.equals(right);
    }

    @Fallback
    protected void orElse(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
