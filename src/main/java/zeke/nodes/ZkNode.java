package zeke.nodes;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ZkNode extends Node {
    public abstract Object execute(VirtualFrame frame);
}
