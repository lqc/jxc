package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class Constant extends Expression {
	
	private Object value;

	public Constant(StaticContainer cont, Type t, Object v) {
		super(cont, t);
		
		value = v;
	}

}
