package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class ZekeRootNode extends RootNode {
    private @Child ZekeNode root;

    public ZekeRootNode(ZekeNode root) {
        super(null);
        this.root = root;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return root.execute(frame);
    }
}