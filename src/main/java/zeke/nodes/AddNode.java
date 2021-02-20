package zeke.nodes;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChild("left")
@NodeChild("right")
public abstract class AddNode extends ZekeNode {
    public static AddNode create(ZekeNode left, ZekeNode right) {
        return AddNodeGen.create(left, right);
    }

    @Specialization
    protected int addInts(int left, int right) {
        return left + right;
    }

    @Fallback
    protected void typeError(Object left, Object right) {
        throw new RuntimeException("type error");
    }
}
