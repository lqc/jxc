package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public abstract class Expression extends Operation 
{
	
	public Expression(StaticContainer cont, Type t) {
		super(cont);
		
		type = t;
	}

	protected Type type;
}
