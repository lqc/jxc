package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class Constant<T> extends Expression {
	
	private T value;

	public Constant(StaticContainer cont, int line, Type t, T v) {
		super(cont, line, t);
		
		value = v;
	}
	
	public T value() {
		return value;
	}

}
