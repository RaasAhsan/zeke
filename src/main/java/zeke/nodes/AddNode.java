package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

public abstract class AddNode extends BinaryNode {
    public static AddNode of(ExpressionNode left, ExpressionNode right) {
        return AddNodeGen.create(left, right);
    }

    @Specialization
    protected int addInts(int left, int right) {
        return left + right;
    }

    @Specialization
    protected String addStrings(String left, String right) {
        return left + right;
    }

    @Fallback
    protected void typeError(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
