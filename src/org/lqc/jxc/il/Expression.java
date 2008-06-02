package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public abstract class Expression<T extends Type> extends Operation 
{
	
	public Expression(StaticContainer cont, int line, T t) {
		super(cont, line);
		
		type = t;
	}

	protected T type;

	/**
	 * @return the type
	 */
	public T getType() {
		return type;
	}
}
