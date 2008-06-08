package org.lqc.jxc.il;

import org.lqc.jxc.types.FunctionType;

public interface Callable {
		
	public Signature<FunctionType> declSignature();	
	public Signature<FunctionType> callSignature();
	
	public Klass container();
	
	public boolean isStatic();
	public boolean isAbstract();
}
