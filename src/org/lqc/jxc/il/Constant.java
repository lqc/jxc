package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.PrimitiveType;

public class Constant<T> extends Expression<PrimitiveType> {
	
	private T value;

	public Constant(StaticContainer<?> cont, int line, T v) {
		super(cont, line, PrimitiveType.forValue(v));
		
		value = v;
	}
	
	public T value() {
		return value;
	}
	
	@Override
	public <S> void visit(ILVisitor<S> v) {
		v.process(this);		
	}
	
	public String toString() {
		if(value instanceof String) 
			return "\"" + value + "\"";
		else 
			return value.toString();
	}
}
