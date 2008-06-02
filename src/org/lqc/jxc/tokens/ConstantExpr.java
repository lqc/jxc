package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;
import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.PrimitiveType;

public class ConstantExpr extends ExprToken<PrimitiveType> {
	
	private Object value;
			
	public ConstantExpr(int l, int col, Object c) { 
		super(l, col, PrimitiveType.forValue(c));
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
