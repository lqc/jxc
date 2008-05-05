package org.lqc.jxc.il;

public class VariableValue extends Expression 
{	
	protected Variable reference;
	
	public VariableValue(StaticContainer cont, Variable v)
	{
		super(cont, v.signature.type);	
		reference = v;
	}

}
