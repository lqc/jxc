package org.lqc.jxc.il;

import org.lqc.jxc.types.FunctionType;

public interface Callable {
	
	/* don't know yet */
	public Signature<FunctionType> callSignature();	
	public StaticContainer container();

}
