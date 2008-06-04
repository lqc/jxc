package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;


public class VariableValue extends Expression<Type> 
{	
	protected Variable<?> reference;
	
	public VariableValue(StaticContainer<?> cont, int line, Variable<?> v)
	{
		super(cont, line, v.signature.type);	
		reference = v;
	}
	
	public Variable<?> reference() {
		return reference;
	}
	
	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}
