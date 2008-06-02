package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;

public class ReturnVoid extends Operation {

	public ReturnVoid(StaticContainer cont, int line) {
		super(cont, line);		
	}

	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}

}
