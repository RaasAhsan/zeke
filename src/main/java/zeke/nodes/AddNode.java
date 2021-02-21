package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.Specialization;

// TODO: This is just a convenience node for now, but ultimately addition will be
// defined in the guest language
public abstract class AddNode extends BinaryNode {
    public static AddNode of(ExpressionNode left, ExpressionNode right) {
        return AddNodeGen.create(left, right);
    }

    @Specialization
    protected int doInt(int left, int right) {
        return left + right;
    }

    @Specialization
    protected String doString(String left, String right) {
        return left + right;
    }

    @Fallback
    protected void orElse(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
