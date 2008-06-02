package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;

public class TypeCast extends ExprToken {
	
	private Type dst;
	private ExprToken e;

	public TypeCast(int l, int c, ExprToken e, Type dst) {
		super(l, c, dst);
				
		this.dst = dst;
		this.e = e;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public Type srcType() {
		return e.getType();		
	}
	
	public Type dstType() {
		return dst;
	}
	
	public ExprToken getExpression() {
		return e;		
	}

}
