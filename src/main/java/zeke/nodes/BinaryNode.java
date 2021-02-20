package zeke.nodes;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild("left")
@NodeChild("right")
public abstract class BinaryNode extends ExpressionNode {
}
