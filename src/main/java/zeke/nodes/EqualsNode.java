package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class EqualsNode extends BinaryNode {
    public static EqualsNode of(ExpressionNode left, ExpressionNode right) {
        return EqualsNodeGen.create(left, right);
    }

    @Specialization
    protected boolean equalsInt(int left, int right) {
        return left == right;
    }

    @Specialization
    protected boolean equalsString(String left, String right) {
        return left.equals(right);
    }

    @Fallback
    protected void typeError(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
