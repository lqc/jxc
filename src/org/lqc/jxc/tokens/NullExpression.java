package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class NullExpression extends Expression {
	
	public NullExpression() {
		super(Type.ANY);	
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}

}
