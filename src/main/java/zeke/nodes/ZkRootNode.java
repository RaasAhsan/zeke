package zeke.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class ZkRootNode extends RootNode {
    private @Child
    ZkNode root;

    public ZkRootNode(ZkNode root) {
        super(null);
        this.root = root;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return root.execute(frame);
    }
}