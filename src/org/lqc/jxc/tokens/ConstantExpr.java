package org.lqc.jxc.tokens;

import org.lqc.jxc.types.PrimitiveType;

public class ConstantExpr extends Expression {
	
	private Object value;
			
	public ConstantExpr(Object c) { 
		super(PrimitiveType.forValue(c));
		value = c;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
}
