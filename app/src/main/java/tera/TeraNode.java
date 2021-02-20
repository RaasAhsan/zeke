package tera;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class TeraNode extends Node {
    public abstract Object execute(VirtualFrame frame);
}
