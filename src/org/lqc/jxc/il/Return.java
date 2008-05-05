package org.lqc.jxc.il;

public class Return extends Operation {
	
	public Expression returnValue;

	public Return(StaticContainer cont, Expression e) {
		super(cont);
		
		returnValue = e;
	}

}
