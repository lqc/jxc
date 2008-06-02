package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;

public class Nop extends Operation {
	public Nop(StaticContainer cont, int line) {
		super(cont, line);		
	}
	
	@Override
	public <T> void visit(ILVisitor<T> v) {
		// v.process(this);			
	}
}
