package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class MultiplyNode extends BinaryNode {
    public static MultiplyNode of(ExpressionNode left, ExpressionNode right) {
        return MultiplyNodeGen.create(left, right);
    }

    @Specialization
    protected int doInt(int left, int right) {
        return left * right;
    }

    @Fallback
    protected void orElse(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
