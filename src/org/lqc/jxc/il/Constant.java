package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.PrimitiveType;

public class Constant<T> extends Expression<PrimitiveType> {
	
	private T value;

	public Constant(StaticContainer cont, int line, 
			PrimitiveType t, T v) {
		super(cont, line, t);
		
		value = v;
	}
	
	public T value() {
		return value;
	}
	
	@Override
	public <S> void visit(ILVisitor<S> v) {
		v.process(this);		
	}
}
