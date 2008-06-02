package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;

public class Return extends Operation {
	
	public Expression returnValue;

	public Return(StaticContainer cont, int line, Expression e) {
		super(cont, line);
		
		returnValue = e;
	}

	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}
