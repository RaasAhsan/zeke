package zeke.nodes;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class ZkRootNode extends RootNode {
    @Child private ZkNode root;

    public ZkRootNode(ZkNode root, FrameDescriptor frameDescriptor) {
        super(null, frameDescriptor);
        this.root = root;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return root.execute(frame);
    }
}