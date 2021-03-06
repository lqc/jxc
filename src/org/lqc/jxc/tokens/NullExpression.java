package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;

public class NullExpression extends ExprToken<Type> {
	
	public NullExpression() {
		super(-1, -1, Type.ANY);	
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}

}
