package org.lqc.jxc.il;


public class VariableValue extends Expression 
{	
	protected Variable reference;
	
	public VariableValue(StaticContainer cont, int line, Variable v)
	{
		super(cont, line, v.signature.type);	
		reference = v;
	}
	
	public Variable reference() {
		return reference;
	}
}
