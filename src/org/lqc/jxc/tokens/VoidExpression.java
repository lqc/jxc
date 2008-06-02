package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;

public class VoidExpression extends ExprToken<Type> {
	
	public VoidExpression() {
		super(-1, -1, Type.VOID);	
	}

	@Override
	public void visitNode(TreeVisitor v) {}

}
