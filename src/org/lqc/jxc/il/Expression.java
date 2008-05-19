package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public abstract class Expression extends Operation 
{
	
	public Expression(StaticContainer cont, int line, Type t) {
		super(cont, line);
		
		type = t;
	}

	protected Type type;

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
}
