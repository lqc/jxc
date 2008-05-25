package org.lqc.jxc.il;

import java.util.Collection;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** Interface for entities that declare static context. */
public interface StaticContainer {
	
	public StaticContainer container();
	
	public Callable get(Signature<FunctionType> t);
	public Callable newFunc(Signature<FunctionType> t,
			Signature<Type>... args);
	
	public Variable get(Signature<Type> t);	
	public Variable newVar(Signature<Type> t);
		
	public Collection<Callable> allFunctions();
	public Collection<Variable> allVariables();
	
	public Label getUniqueLabel();
	
	public String absolutePath();
	public String name();	
}
