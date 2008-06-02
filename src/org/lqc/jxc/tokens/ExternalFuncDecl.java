package org.lqc.jxc.tokens;

import org.lqc.jxc.il.Callable;

public class ExternalFuncDecl extends FunctionDecl {
	
	private Callable f;

	public ExternalFuncDecl(Callable f) {
		super(-1, -1, f.declSignature().name, f.declSignature().type);
		this.f = f;		
	}
	
	public Callable getCallable() {
		return f;
	}
	
}
