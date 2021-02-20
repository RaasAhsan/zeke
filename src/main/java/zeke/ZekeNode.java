package zeke;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class ZekeNode extends Node {
    public abstract Object execute(VirtualFrame frame);
}
