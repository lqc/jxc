package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class TypeCast extends Expression {
	
	private Type dst;
	private Expression e;

	public TypeCast(int l, int c, Expression e, Type dst) {
		super(l, c, dst);
		
		/* XXX: make additional check */
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
	
	public Expression getExpression() {
		return e;		
	}

}
