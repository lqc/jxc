package org.lqc.jxc.il;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** Interface for entities that declare static context. */
public interface StaticContainer {
	
	public StaticContainer parent();
	
	public Function getFunction(Signature<FunctionType> t);
	public Function newFunction(Signature<FunctionType> t,
			Signature<Type>... args);
	
	public Variable getVariable(Signature<Type> t);	
	public Variable newVariable(Signature<Type> t);
 

}
