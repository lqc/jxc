package org.lqc.jxc.tokens;

import org.lqc.jxc.types.FunctionType;

public class MethodRef implements CallableRef {
	
	private VarDecl<? extends FunctionType> instance;
	private String callName;
	private FunctionType callType;
	
	public MethodRef(VarDecl<? extends FunctionType> var, 
			String callName, FunctionType callType)
	{
		this.instance = var;
		this.callName = callName;
		this.callType = callType;		
	}
	

	public FunctionType getType() {
		return this.instance.entityType;		
	}
	
	public VarDecl<? extends FunctionType> getInstance() {
		return this.instance;
	}

}
