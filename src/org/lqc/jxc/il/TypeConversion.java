package org.lqc.jxc.il;

import org.lqc.jxc.types.Type;

public class TypeConversion extends Expression {
	
	private Type dst;
	private Expression innerExpr;

	public TypeConversion(StaticContainer slink, int line, 
			Expression e, Type dst) {
		super(slink, line, dst);

		this.dst = dst;
		this.innerExpr = e;
	}
	
	public Type srcType() {
		return innerExpr.type;		
	}
	
	public Type dstType() {
		return dst;
	}	
	
	public Expression getInnerExpr() {
		return innerExpr;
	}

}
