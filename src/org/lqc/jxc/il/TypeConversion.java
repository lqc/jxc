package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;

public class TypeConversion<S extends Type,T extends Type> extends Expression<T> {
	
	private Type dst;
	private Expression<S> innerExpr;

	public TypeConversion(StaticContainer slink, int line, 
			Expression<S> e, T dst) {
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
	
	public Expression<S> getInnerExpr() {
		return innerExpr;
	}

	@Override
	public <A> void visit(ILVisitor<A> v) {
		v.process(this);		
	}
}
