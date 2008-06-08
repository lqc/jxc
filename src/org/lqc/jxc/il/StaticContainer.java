package org.lqc.jxc.il;

import java.util.Collection;

import org.lqc.jxc.types.FunctionType;
import org.lqc.jxc.types.Type;

/** Interface for entities that declare static context. */
public interface StaticContainer<T extends StaticContainer<T>> {
	
	public StaticContainer<?> container();
	
	public Callable get(Signature<FunctionType> t);
	public Function newFunc(int line, Signature<FunctionType> t,
			boolean isstatic);
	public void remove(Callable f); 
	
	public Variable<?> get(Signature<Type> t);	
	public Variable<?> newVar(Signature<? extends Type> t, boolean islocal);
		
	public Collection<Callable> allCallables();
	public Collection<Variable<?>> allVariables();
	
	public Label getUniqueLabel();
	public String getUniqueLambdaName();
		
	public Klass getNearestKlass();
	public String getAbsoluteName();
	
}
