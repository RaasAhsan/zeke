package tera;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class TeraRootNode extends RootNode {
    private @Child TeraNode root;

    public TeraRootNode(TeraNode root) {
        super(null);
        this.root = root;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return root.execute(frame);
    }
}