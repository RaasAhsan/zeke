package zeke;

import com.oracle.truffle.api.dsl.TypeSystem;
import zeke.runtime.ZkFunction;

@TypeSystem({boolean.class, ZkFunction.class})
public abstract class ZkTypes {
}
